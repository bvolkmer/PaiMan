package de.x4fyr.paiman.lib.adapter

import com.couchbase.lite.NetworkReachabilityManager
import com.couchbase.lite.storage.SQLiteStorageEngineFactory
import java.io.File
import java.io.InputStream
import java.util.concurrent.Future

/** Android implementation of [GoogleDriveStorageAdapter] using PlayServices*/
class AndroidGoogleDriveStorageAdapter: GoogleDriveStorageAdapter {
    /**
     * Replicators call this to get the NetworkReachabilityManager, and they register/unregister
     * themselves to receive network reachability callbacks.
     *
     * If setNetworkReachabilityManager() was called prior to this, that instance will be used.
     * Otherwise, the context will create a new default reachability manager and return that.
     */
    override fun getNetworkReachabilityManager(): NetworkReachabilityManager {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * The files dir.  On Android implementation, simply proxies call to underlying Context
     */
    override fun getFilesDir(): File {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Get the SQLiteStorageEngineFactory, or null if none has been set, in which case
     * the default will be used.
     */
    override fun getSQLiteStorageEngineFactory(): SQLiteStorageEngineFactory {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Override the default behavior and set your own NetworkReachabilityManager subclass,
     * which allows you to completely control how to respond to network reachability changes
     * in your app affects the replicators that are listening for change events.
     */
    override fun setNetworkReachabilityManager(networkReachabilityManager: NetworkReachabilityManager?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Return User-Agent value
     */
    override fun getUserAgent(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Get temporary directory. The temporary directory will be used to store temporary files.
     */
    override fun getTempDir(): File {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** Get thumbnail of image with [id] */
    override suspend fun getThumbnail(id: String): InputStream {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** Get image as [InputStream] from storage */
    override suspend fun getImage(id: String): InputStream {
        TODO("not implemented") //TODO: not implemented
    }

    /** Save image from [InputStream] to Storage */
    override suspend fun saveImage(image: InputStream): String {
        TODO("not implemented") //TODO: not implemented
    }

    /** Delete image from storage */
    override suspend fun deleteImage(id: String) {
        TODO("not implemented") //TODO: not implemented
    }
}