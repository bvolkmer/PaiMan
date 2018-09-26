package de.x4fyr.paiman.lib.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.couchbase.lite.android.AndroidContext
import de.x4fyr.paiman.util.LRUCacheMap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

/**
 * Android
 */
class AndroidStorageAdapter(context: Context) : StorageAdapter, AndroidContext(context) {

    private val privateDir = context.getExternalFilesDir(null)
    private val publicDir = Environment.getExternalStorageDirectory().resolve(publicSubDir)

    private val imagesPath = publicDir.resolve(imagesDir)
    private val thumbnailPath = privateDir.resolve(thumbnailDir)
    private val couchbasePath = privateDir.resolve(couchbaseDir)

    private val imageCache = LRUCacheMap<String, ByteArray>(1000)
    private val thumbnailCache = LRUCacheMap<String, ByteArray>(1000)

    init {
        if (!publicDir.exists()) publicDir.mkdir()
        if (!couchbasePath.exists()) couchbasePath.mkdir()
        if (!imagesPath.exists()) imagesPath.mkdir()
        if (!thumbnailPath.exists()) thumbnailPath.mkdir()
    }

    /** Save image from [InputStream] to Storage */
    override suspend fun saveImage(image: InputStream): String {
        val id = generateUniqueID()
        writeImageToPath(image, imagesPath.resolve(id))
        return id
    }

    /** Delete image from storage */
    override suspend fun deleteImage(id: String) {
        imageCache.remove(id)
        thumbnailCache.remove(id)
        thumbnailPath.resolve(id)
                .takeIf { it.exists() }
                ?.also { it.delete() }
        imagesPath.resolve(id)
                .takeIf { it.exists() }
                ?.also { it.delete() }
                ?: throw StorageAdapter.StorageException.EntityDoesNotExist(id)
    }

    /** Get thumbnail of image with [id] */
    override suspend fun getThumbnail(id: String): InputStream = if (thumbnailCache.containsKey(id)) {
        thumbnailCache[id]!!.inputStream()
    } else {
        if (thumbnailPath.resolve(id).exists()) {
            val byteArray = thumbnailPath.resolve(id).inputStream().readBytes()
            thumbnailCache[id] = byteArray
            byteArray.inputStream()
        } else {
            val outStream = ByteArrayOutputStream()
            getImage(id)
                    .let { BitmapFactory.decodeStream(it) }
                    .let { Bitmap.createScaledBitmap(it, 100, 100, false) }
                    .compress(Bitmap.CompressFormat.JPEG, 98, outStream)
            val byteArray = outStream.toByteArray()
            thumbnailPath.resolve(id).writeBytes(byteArray)
            thumbnailCache[id] = byteArray
            byteArray.inputStream()
        }
    }

    /** Write [image] stream to [destinationPath] */
    private fun writeImageToPath(image: InputStream, destinationPath: File) {
        if (destinationPath.exists()) {
            destinationPath.delete()
            destinationPath.createNewFile()
        }
        val outputStream = FileOutputStream(destinationPath)
        image.copyTo(outputStream)
        outputStream.close()
    }

    /** Generate random image id that does not exist in [imagesDir]*/
    private fun generateUniqueID(): String {
        var id = UUID.randomUUID().toString()
        var path = imagesPath.resolve(id)
        while (path.exists()) {
            id = UUID.randomUUID().toString()
            path = imagesPath.resolve(id)
        }
        return id
    }

    /** Get image as [InputStream] from storage */
    override suspend fun getImage(id: String): InputStream = if (imageCache.containsKey(id)) {
        imageCache[id]!!.inputStream()
    } else {
        val fileByteArray = (imagesPath.resolve(id)
                .takeIf { it.exists() }?.inputStream()
                ?: throw StorageAdapter.StorageException.EntityDoesNotExist(id)).readBytes()
        imageCache[id] = fileByteArray
        fileByteArray.inputStream()
    }

    /** See [com.couchbase.lite.Context]. Overridden to modify couchbase path to central storage */
    override fun getFilesDir(): File = couchbasePath


    companion object {
        private const val publicSubDir = "Paiman"
        private const val couchbaseDir = "couchbase"
        private const val imagesDir = "images"
        private const val thumbnailDir = "thumbnails"
    }
}