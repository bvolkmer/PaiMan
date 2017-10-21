package de.x4fyr.paiman.lib.adapter

import java.io.InputStream
import java.util.concurrent.Future

/**
 * General storage service for storing images
 */
interface StorageAdapter {

    /** Get image as [InputStream] from storage */
    @Throws(StorageException::class)
    suspend fun getImage(id: String): Future<InputStream>

    /** Save image from [InputStream] to Storage */
    @Throws(StorageException::class)
    suspend fun saveImage(image: InputStream, id: String? = null): String

    /** Delete image from storage */
    @Throws(StorageException::class)
    suspend fun deleteImage(id: String)

    /** Exceptions thrown in [StorageAdapter] */
    sealed class StorageException: Exception {
        constructor(msg: String): super(msg)
        constructor(msg: String, cause: Throwable): super(msg, cause)

        /** Unspecific Storage Exception */
        class General: StorageException{
            constructor(msg: String): super(msg)
            constructor(msg: String, cause: Throwable): super(msg, cause)
        }

        /** Exception thrown if queried an storage entity does not exist*/
        class EntityDoesNotExist(msg: String): StorageException(msg)
    }


}