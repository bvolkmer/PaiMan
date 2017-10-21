package de.x4fyr.paiman.lib.provider

import com.couchbase.lite.JavaContext
import de.x4fyr.paiman.lib.adapter.GoogleDriveStorageAdapter
import java.io.InputStream
import java.util.concurrent.Future

/**
 * ServiceProvider implementation for desktop platform
 */
class DesktopServiceProvider : ServiceProvider(JavaContext(), StubStorageAdapter())

/** Stub Implementation for old desktop client */
class StubStorageAdapter: GoogleDriveStorageAdapter {
    /** Get image as [InputStream] from storage */
    override suspend fun getImage(id: String): Future<InputStream> {
        TODO("not implemented") //TODO: not implemented
    }

    /** Save image from [InputStream] to Storage */
    suspend override fun saveImage(image: InputStream, id: String?): String {
        TODO("not implemented") //TODO: not implemented
    }

    /** Delete image from storage */
    suspend override fun deleteImage(id: String) {
        TODO("not implemented") //TODO: not implemented
    }

}
