package de.x4fyr.paiman.lib.adapter

import android.app.Activity
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
import com.google.android.gms.drive.MetadataChangeSet
import com.google.android.gms.drive.query.Filters
import com.google.android.gms.drive.query.Query
import com.google.android.gms.drive.query.SearchableField
import de.x4fyr.paiman.app.BaseActivity
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.UUID
import kotlin.properties.Delegates

/**
 * Android implementation fo the Google Drive Storage adapter
 */
class AndroidGoogleDriveStorageAdapter(private val context: Context): GoogleDriveStorageAdapter {

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

    /** Connect [GoogleApiClient] with current active activity */
    fun connect(activeActivity: BaseActivity) {
        Log.i(this::class.simpleName, "Connecting with new activity $activeActivity")
        currentActiveActivity = activeActivity
        activeActivity.addActivityResultHandler(this) { requestCode, resultCode, _ ->
            if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == Activity.RESULT_OK) {
                Log.i(this::class.simpleName, "GoogleApiClient resolution succeeded. Trying to connect again.")
                googleApiClient!!.connect()
            }
        }

        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(context)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addOnConnectionFailedListener {
                        Log.e(this::class.simpleName, "GoogleApiClient connection failed: $it")
                        if (!it.hasResolution()) {
                            GoogleApiAvailability.getInstance()
                                    .getErrorDialog(currentActiveActivity, it.errorCode, 0).show()
                        }
                        try {
                            it.startResolutionForResult(currentActiveActivity, REQUEST_CODE_RESOLUTION)
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

    /** Disconnect [GoogleApiClient] also un-setting the current active activity*/
    fun disconnect() {
        Log.i(this::class.simpleName, "Disconnecting with old activity $currentActiveActivity")
        googleApiClient?.disconnect()
        currentActiveActivity?.removeActivityResultHandler(this)
        //currentActiveActivity = null
    }

    /** See [GoogleDriveStorageAdapter.getImage]] */
    override suspend fun getImage(id: String): InputStream {
        connect(currentActiveActivity!!)
        while (googleApiClient?.isConnected == false) {
            while (googleApiClient?.isConnecting == true) {
            }
            if (googleApiClient?.isConnected == false) {
                connect(currentActiveActivity!!)
                Log.w(this::class.simpleName, "Trying to reconnect")
            }
        }
        if (googleApiClient?.isConnected == true) {
            val appFolder = Drive.DriveApi.getAppFolder(googleApiClient)
            val queryResult = appFolder.queryChildren(googleApiClient, Query.Builder()
                    .addFilter(Filters.eq(SearchableField.TITLE, "$id.png"))
                    .build()).await().metadataBuffer
            if (queryResult.count >= 1) {
                val fd = queryResult[0].driveId.asDriveFile().open(googleApiClient, DriveFile.MODE_READ_ONLY,
                        { _, _ -> }).await().driveContents.parcelFileDescriptor
                val result = ParcelFileDescriptor.AutoCloseInputStream(fd)
                Log.i(this::class.simpleName, "Successfully got image $id")
                return result
            } else {
                throw error("Failed to get image, as there is no image with id $id found")
            }
        } else {
            throw error("Failed to get image, as GoogleApiClient is not connected")
        }
    }

    /** See [GoogleDriveStorageAdapter.saveImage]] */
    override suspend fun saveImage(image: InputStream): String {
        connect(currentActiveActivity!!)
        if (googleApiClient?.isConnected == true) {
            val appFolder = Drive.DriveApi.getAppFolder(googleApiClient)
            val result = Drive.DriveApi.newDriveContents(googleApiClient).await()
            if (result.status.isSuccess) {
                val markableStream: InputStream = if (image.markSupported()) image
                else image.buffered(image.available())
                markableStream.mark(Int.MAX_VALUE)
                val id = UUID.nameUUIDFromBytes(markableStream.readBytes()).toString()
                markableStream.reset()
                val compressedImageStream = ByteArrayOutputStream()
                val bitmap = BitmapFactory.decodeStream(markableStream)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, compressedImageStream)
                result.driveContents.outputStream.write(compressedImageStream.toByteArray())
                val metadataChangeSet = MetadataChangeSet.Builder().setMimeType("image/png").setTitle("$id.png").build()
                val createResult = appFolder.createFile(googleApiClient, metadataChangeSet, result.driveContents)
                        .await()
                if (!createResult.status.isSuccess) {
                    throw error("Failed to create file")
                } else {
                    Log.i(this::class.simpleName, "Successfully saved image $id")
                    return id
                }
            } else {
                throw error("Failed to create new content")
            }
        } else {
            throw error("Failed to save image, as GoogleApiClient is not connected")
        }
    }

    /** See [GoogleDriveStorageAdapter.deleteImage]] */
    override suspend fun deleteImage(id: String) {
        connect(currentActiveActivity!!)
        if (googleApiClient?.isConnected == true) {
            val appFolder = Drive.DriveApi.getAppFolder(googleApiClient)
            val queryResult = appFolder.queryChildren(googleApiClient, Query.Builder()
                    .addFilter(Filters.eq(SearchableField.TITLE, "$id.png"))
                    .build()).await().metadataBuffer
            if (queryResult.count == 1) {
                async(CommonPool) {
                    queryResult[0].driveId.asDriveFile().delete(googleApiClient)
                }
                Log.i(this::class.simpleName, "Successfully deleted image $id")
            } else {
                throw error("Image could not be deleted, as there is no image with id [$id] " +
                        "found.")
            }
        } else {
            throw error("Image could not be deleted, as GoogleApiClient is not connected")
        }
    }

    private fun error(msg: String): StorageAdapter.StorageException {
        Log.e(this::class.simpleName, msg)
        return StorageAdapter.StorageException(msg)
    }
}
