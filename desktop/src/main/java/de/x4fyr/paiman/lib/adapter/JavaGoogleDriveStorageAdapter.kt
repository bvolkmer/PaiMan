package de.x4fyr.paiman.lib.adapter

import java.io.InputStream
import java.util.concurrent.Future

/** Java implementation of [GoogleDriveStorageAdapter] using Drive API Client Library for Java
 *
 * TODO: This is a stub
 */
class JavaGoogleDriveStorageAdapter: GoogleDriveStorageAdapter {
    /** Get image as [InputStream] from storage */
    suspend override fun getImage(id: String): Future<InputStream> {
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