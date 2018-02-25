package de.x4fyr.paiman.lib.adapter

import java.io.InputStream
import java.util.concurrent.Future

/** Android implementation of [GoogleDriveStorageAdapter] using PlayServices*/
class AndroidGoogleDriveStorageAdapter: GoogleDriveStorageAdapter {
    /** Get image as [InputStream] from storage */
    suspend override fun getImage(id: String): Future<InputStream> {
        TODO("not implemented") //TODO: not implemented
    }

    /** Save image from [InputStream] to Storage */
    suspend override fun saveImage(image: InputStream): String {
        TODO("not implemented") //TODO: not implemented
    }

    /** Delete image from storage */
    suspend override fun deleteImage(id: String) {
        TODO("not implemented") //TODO: not implemented
    }
}