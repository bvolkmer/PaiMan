package de.x4fyr.paiman.lib.adapter

import java.io.InputStream

/**
 * General storage service for storing images
 */
interface StorageAdapter {

    /** Get image as [InputStream] from storage */
    @Throws(StorageException::class)
    suspend fun getImage(id: String): InputStream

    /** Save image from [InputStream] to Storage */
    @Throws(StorageException::class)
    suspend fun saveImage(image: InputStream): String

    /** Delete image from storage */
    @Throws(StorageException::class)
    suspend fun deleteImage(id: String)

    /** Exceptions thrown in [StorageAdapter] */
    class StorageException: Exception {
        constructor(msg: String): super(msg)
        constructor(msg: String, cause: Throwable): super(msg, cause)
        constructor(): super()
    }
}