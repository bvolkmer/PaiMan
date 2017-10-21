package de.x4fyr.paiman.lib.adapter

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.IntentSender
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.drive.Drive
import com.google.android.gms.drive.DriveFile
import com.google.android.gms.drive.MetadataBuffer
import com.google.android.gms.drive.MetadataChangeSet
import com.google.android.gms.drive.query.Filters
import com.google.android.gms.drive.query.Query
import com.google.android.gms.drive.query.SearchableField
import de.x4fyr.paiman.app.BaseActivity
import de.x4fyr.paiman.app.error
import de.x4fyr.paiman.lib.SettableFuture
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.UUID
import java.util.concurrent.Future
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock
import kotlin.properties.Delegates

/**
 * Android implementation fo the Google Drive Storage adapter
 */
class AndroidGoogleDriveStorageAdapter(private val context: Context): GoogleDriveStorageAdapter, Application.ActivityLifecycleCallbacks {

    private companion object {
        /** Random number for Request resolution */
        const val REQUEST_CODE_RESOLUTION = 1
    }

    private val ID = UUID.randomUUID().toString()

    private var googleApiClient: GoogleApiClient?  by Delegates.observable<GoogleApiClient?>(null) { _, old, new ->
        Log.i(this::class.simpleName, "$ID: GoogleApiClient changed: $old -> $new")
    }

    private var currentActiveActivity: BaseActivity?  by Delegates.observable<BaseActivity?>(null) { _, old, new ->
        Log.i(this::class.simpleName, "$ID: Current active activity changed: $old -> $new")
    }

    private val lock = ReentrantReadWriteLock()

    init {
        (context.applicationContext as Application).registerActivityLifecycleCallbacks(this)
    }

    /** Connect [GoogleApiClient]*/
    private fun connect() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(context)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addOnConnectionFailedListener {
                        Log.e(this::class.simpleName, "GoogleApiClient connection failed: $it")
                        if (!it.hasResolution()) {
                            if (currentActiveActivity != null) GoogleApiAvailability.getInstance()
                                    .getErrorDialog(currentActiveActivity, it.errorCode, 0).show()
                        }
                        try {
                            if (currentActiveActivity != null) it.startResolutionForResult(currentActiveActivity,
                                    REQUEST_CODE_RESOLUTION)
                        } catch (e: IntentSender.SendIntentException) {
                            Log.e(this::class.simpleName, "Exception starting intent: $e")
                        }
                    }
                    .addConnectionCallbacks(object: GoogleApiClient.ConnectionCallbacks {
                        /** See [GoogleApiClient.ConnectionCallbacks.onConnected] */
                        override fun onConnected(p0: Bundle?) {
                            Log.i(this::class.simpleName, "GoogleApiClient connected")
                        }

                        /** See [GoogleApiClient.ConnectionCallbacks.onConnectionSuspended] */
                        override fun onConnectionSuspended(p0: Int) {
                            Log.i(this::class.simpleName, "GoogleApiClient suspended")
                        }

                    })
                    .build()
        }
        googleApiClient!!.connect()
    }

    /** See [GoogleDriveStorageAdapter.getImage]] */
    @Throws(StorageAdapter.StorageException::class)
    override suspend fun getImage(id: String): Future<InputStream> {
        val settableFuture = SettableFuture<InputStream>()
        lock.readLock().withLock {
            for (j in 1..10) {
                connect()
                if (googleApiClient?.isConnected == false) {
                    while (googleApiClient?.isConnecting == true) {
                        Thread.sleep(1000)
                    }
                    if (googleApiClient?.isConnected == false) {
                        connect()
                        Log.w(this::class.simpleName, "Trying to reconnect")
                    }
                } else if (googleApiClient?.isConnected == true) break
            }
            if (googleApiClient?.isConnected == true) {
                val appFolder = Drive.DriveApi.getAppFolder(googleApiClient)
                appFolder.queryChildren(googleApiClient, Query.Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, "$id.png"))
                        .build()).setResultCallback {
                    Log.d("${this::class.simpleName}::getImage", "Got queryResult")
                    if (!it.status.isSuccess) {
                        settableFuture.except(
                                error("Failed to get image queryResult with status: ${it.status.statusCode}\n${it.status.statusMessage}"))
                    } else {
                        val metadataBuffer: MetadataBuffer = it.metadataBuffer!!
                        if (metadataBuffer.count >= 1) {
                            val fid = metadataBuffer[0].driveId
                            metadataBuffer.release()
                            fid.asDriveFile().open(googleApiClient, DriveFile.MODE_READ_ONLY,
                                    { _, _ -> }).setResultCallback {
                                Log.d("${this::class.simpleName}::getImage", "Got fileResult")
                                if (!it.status.isSuccess) {
                                    settableFuture.except(
                                            error("Failed to get fileResult with status: ${it.status.statusCode}\n${it.status.statusMessage}"))
                                } else {
                                    val fd: ParcelFileDescriptor = it.driveContents!!.parcelFileDescriptor
                                    Log.d("${this::class.simpleName}::getImage", "Successfully got image $id")
                                    settableFuture.set(ParcelFileDescriptor.AutoCloseInputStream(fd))
                                }
                            }
                        } else {
                            metadataBuffer.release()
                            Log.e(this@AndroidGoogleDriveStorageAdapter::class.simpleName,
                                    "Failed to get image, as there is no image with id $id found")
                            settableFuture.except(StorageAdapter.StorageException.EntityDoesNotExist(
                                    "Failed to get image, as there is no image with id $id found"))
                        }
                    }
                }
            } else {
                settableFuture.except(error("Failed to get image, as GoogleApiClient is not connected"))
            }
        }
        return settableFuture
    }

    /** See [GoogleDriveStorageAdapter.saveImage]] */
    @Throws(StorageAdapter.StorageException::class)
    override suspend fun saveImage(image: InputStream, id: String?): String {
        lock.writeLock().withLock {
            for (j in 1..10) {
                connect()
                if (googleApiClient?.isConnected == false) {
                    while (googleApiClient?.isConnecting == true) {
                        Thread.sleep(1000)
                    }
                    if (googleApiClient?.isConnected == false) {
                        connect()
                        Log.w(this::class.simpleName, "Trying to reconnect")
                    }
                } else if (googleApiClient?.isConnected == true) break
            }
            if (googleApiClient?.isConnected == true) {
                val appFolder = Drive.DriveApi.getAppFolder(googleApiClient)
                val result = Drive.DriveApi.newDriveContents(googleApiClient).await()
                if (result.status.isSuccess) {
                    //Generate id
                    val newId: String
                    val markableStream: InputStream = if (image.markSupported()) image
                    else image.buffered(image.available())
                    markableStream.mark(Int.MAX_VALUE)
                    if (id != null) {
                        newId = id
                    } else {
                        newId = UUID.nameUUIDFromBytes(markableStream.readBytes()).toString()
                        markableStream.reset()
                    }
                    val compressedImageStream = ByteArrayOutputStream()
                    val bitmap = BitmapFactory.decodeStream(markableStream)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, compressedImageStream)
                    result.driveContents.outputStream.write(compressedImageStream.toByteArray())
                    val metadataChangeSet = MetadataChangeSet.Builder().setMimeType("image/png").setTitle(
                            "$newId.png").build()
                    val createResult = appFolder.createFile(googleApiClient, metadataChangeSet,
                            result.driveContents)
                            .await()
                    if (!createResult.status.isSuccess) {
                        lock.writeLock().unlock()
                        throw error("Failed to create file")
                    } else {
                        Log.i(this::class.simpleName, "Successfully saved image $newId")
                        return newId
                    }
                } else {
                    throw error(
                            "Failed to create new content with status: ${result.status.statusCode}\n${result.status
                                    .statusMessage}")
                }
            } else {
                throw error("Failed to save image, as GoogleApiClient is not connected")
            }
        }
    }

    /** See [GoogleDriveStorageAdapter.deleteImage]] */
    @Throws(StorageAdapter.StorageException::class)
    override suspend fun deleteImage(id: String) {
        lock.writeLock().withLock {
            for (j in 1..10) {
                connect()
                if (googleApiClient?.isConnected == false) {
                    while (googleApiClient?.isConnecting == true) {
                        Thread.sleep(1000)
                    }
                    if (googleApiClient?.isConnected == false) {
                        connect()
                        Log.w(this::class.simpleName, "Trying to reconnect")
                    }
                } else if (googleApiClient?.isConnected == true) break
            }
            if (googleApiClient?.isConnected == true) {
                val appFolder = Drive.DriveApi.getAppFolder(googleApiClient)
                val queryResult = async(CommonPool) {
                    appFolder.queryChildren(googleApiClient, Query.Builder()
                            .addFilter(Filters.eq(SearchableField.TITLE, "$id.png"))
                            .build()).await()
                }.await().metadataBuffer //To prohibit error when cast in UI CoroutineContext
                if (queryResult.count == 1) {
                    async(CommonPool) {
                        queryResult[0].driveId.asDriveFile().delete(googleApiClient)
                    }
                    Log.i(this::class.simpleName, "Successfully deleted image $id")
                    return
                } else {
                    throw error("Image could not be deleted, as there is no image with id [$id] " +
                            "found.")
                }
            } else {
                throw error("Image could not be deleted, as GoogleApiClient is not connected")
            }
        }
    }

    /** Register activity for api connection error resolution */
    override fun onActivityResumed(activity: Activity) {
        if (activity is BaseActivity) {
            currentActiveActivity = activity
            activity.addActivityResultHandler(this) { requestCode, resultCode, _ ->
                if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == Activity.RESULT_OK) {
                    Log.i(this::class.simpleName, "GoogleApiClient resolution succeeded. Trying to connect again.")
                    googleApiClient!!.connect()
                }
            }
        }
    }

    /** Unregister activity for api connection error resolution */
    override fun onActivityPaused(activity: Activity) {
        if (activity == currentActiveActivity && activity is BaseActivity) {
            currentActiveActivity = null
            activity.removeActivityResultHandler(this)
        }
    }

    /** Stub */
    override fun onActivityDestroyed(activity: Activity?) {
    }

    /** Stub */
    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    /** Stub */
    override fun onActivityStopped(activity: Activity) {
    }

    /** Stub */
    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }

    /** Stub */
    override fun onActivityStarted(activity: Activity) {
    }

}

