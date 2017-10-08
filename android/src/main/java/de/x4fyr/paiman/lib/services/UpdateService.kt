package de.x4fyr.paiman.lib.services

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.content.FileProvider
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import de.x4fyr.paiman.R
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.json.JSONArray
import org.json.JSONObject
import org.threeten.bp.LocalDate
import java.io.File
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.withLock

/**
 * Service handling updates and versions of this app
 */
@Singleton
class UpdateService @Inject constructor(private val context: Context) {

    /** Current running version of this app */
    val currentVersion: SemanticVersion by lazy {
        SemanticVersion.of(
                context.packageManager.getPackageInfo(context.packageName, 0).versionName
                        .split("-")[0])
    }.also { Log.d(this::class.simpleName, "Current version name: $it") }

    /** Latest upstream version as GitHub Release */
    val latestVersion: Deferred<SemanticVersion>
        get() = async(CommonPool) {
            SemanticVersion.of(getNewestReleaseJSON().getString("name").removePrefix("v"))
        }

    private val lock = ReentrantLock()
    private var responseCache: Pair<LocalDate, JSONObject>? = null

    private val downloadManager = this@UpdateService.context.getSystemService(
            Context.DOWNLOAD_SERVICE) as DownloadManager

    private suspend fun getNewestReleaseJSON(): JSONObject {
        lock.withLock {
            val cache = responseCache
            if (cache != null && cache.first == LocalDate.now()) return cache.second
            else {
                val queue = Volley.newRequestQueue(this@UpdateService.context)
                val baseUrl = "https://api.github.com"
                val future = RequestFuture.newFuture<JSONArray>()
                val request = JsonArrayRequest(Request.Method.GET, "$baseUrl/repos/bvolkmer/Paiman/releases", null,
                        future,
                        future)
                queue.add(request)
                try {
                    return future.get().getJSONObject(0).also {
                        responseCache = Pair(LocalDate.now(), it)
                    }
                } catch (e: Exception) {
                    Log.e(this@UpdateService::class.simpleName, "Error getting latestVersion")
                    throw e
                }
            }
        }
    }

    private val downloadComplete = object: BroadcastReceiver() {
        /**
         * This method is called when the BroadcastReceiver is receiving an Intent
         * broadcast.  During this time you can use the other methods on
         * BroadcastReceiver to view/modify the current result values.  This method
         * is always called within the main thread of its process, unless you
         * explicitly asked for it to be scheduled on a different thread using
         * [android.content.Context.registerReceiver]. When it runs on the main
         * thread you should
         * never perform long-running operations in it (there is a timeout of
         * 10 seconds that the system allows before considering the receiver to
         * be blocked and a candidate to be killed). You cannot launch a popup dialog
         * in your implementation of onReceive().
         *
         *
         * **If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
         * then the object is no longer alive after returning from this
         * function.** This means you should not perform any operations that
         * return a result to you asynchronously. If you need to perform any follow up
         * background work, schedule a [android.app.job.JobService] with
         * [android.app.job.JobScheduler].
         *
         * If you wish to interact with a service that is already running and previously
         * bound using [bindService()][android.content.Context.bindService],
         * you can use [.peekService].
         *
         *
         * The Intent filters used in [android.content.Context.registerReceiver]
         * and in application manifests are *not* guaranteed to be exclusive. They
         * are hints to the operating system about how to find suitable recipients. It is
         * possible for senders to force delivery to specific recipients, bypassing filter
         * resolution.  For this reason, [onReceive()][.onReceive]
         * implementations should respond only to known actions, ignoring any unexpected
         * Intents that they may receive.
         *
         * @param context The Context in which the receiver is running.
         * @param intent The Intent being received.
         */
        override fun onReceive(context: Context, intent: Intent) {
            Log.e(this::class.simpleName, "Downloaded Update to file ${intent.data}")
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent.action) {
                val query = DownloadManager.Query()
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                        val filePath = File(File(context.externalCacheDir, "update"), "update.apk")
                        val uri = FileProvider.getUriForFile(context.applicationContext,
                                "de.x4fyr.paiman.fileProvider", filePath)
                        val installIntent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/vnd.android.package-archive")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.packageManager.queryIntentActivities(installIntent, PackageManager.MATCH_DEFAULT_ONLY)
                                .map { it.activityInfo.packageName }
                                .forEach {
                                    context.grantUriPermission(it, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                        context.startActivity(installIntent)
                    }
                }
            }
        }
    }

    /** If this app is at the newest version */
    suspend fun isNewestVersion(): Boolean = currentVersion >= latestVersion.await()

    /** Start the update action */
    fun requestUpdate() {
        launch(UI) {
            val updateDir = File(this@UpdateService.context.externalCacheDir, "update").apply { mkdirs() }
            val updateFile = File(updateDir, "update.apk").also {
                if (it.exists()) it.delete()
            }
            val array = getNewestReleaseJSON().getJSONArray("assets")
            var url: String? = null
            (0 until array.length()).map { array.getJSONObject(it) }
                    .filter { it.getString("content_type") == "application/vnd.android.package-archive" }
                    .forEach { url = it.getString("browser_download_url") }
            val downloadRequest = DownloadManager.Request(Uri.parse(url!!)).apply {
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
                setVisibleInDownloadsUi(false)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                setVisibleInDownloadsUi(false)
                setTitle(this@UpdateService.context.getString(R.string.update_download_title))
                setDescription(this@UpdateService.context.getString(R.string.update_download_description))
                setDestinationUri(Uri.fromFile(updateFile))
                setMimeType("application/vnd.android.package-archive")
            }
            this@UpdateService.context.registerReceiver(downloadComplete,
                    IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            downloadManager.enqueue(downloadRequest)
        }
    }


}

/** Container class for semantic version information */
data class SemanticVersion(val major: Int, val minor: Int, val patch: Int): Comparable<SemanticVersion> {

    companion object {
        /** Create [SemanticVersion] of dot-separated string like: major.minor.patch */
        @Throws(IllegalArgumentException::class)
        fun of(versionString: String): SemanticVersion =
                versionString.split(".").mapNotNull { it.toIntOrNull() }.takeIf { it.size == 3 }?.let {
                    SemanticVersion(it[0], it[1], it[2])
                } ?: throw IllegalArgumentException("Wrong number of (sub-)versions or non number (sub-)versions")
    }

    /** See [Any.equals] */
    override fun equals(other: Any?): Boolean = if (other is SemanticVersion) {
        major == other.major && minor == other.minor && patch == other.patch
    } else false


    /** See [Comparable.compareTo] */
    operator override fun compareTo(other: SemanticVersion): Int = when {
        major != other.major -> major.compareTo(other.major)
        minor != other.minor -> minor.compareTo(other.minor)
        else -> patch.compareTo(other.patch)
    }

    /** See [Any.toString] */
    override fun toString(): String = "$major.$minor.$patch"

    /** See [Any.hashCode] */
    override fun hashCode(): Int {
        var result = major
        result = 31*result + minor
        result = 31*result + patch
        return result
    }
}