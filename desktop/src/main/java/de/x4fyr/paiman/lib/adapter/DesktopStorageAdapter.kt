package de.x4fyr.paiman.lib.adapter

import com.couchbase.lite.JavaContext
import de.x4fyr.paiman.util.LRUCacheMap
import net.coobird.thumbnailator.Thumbnails
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

/** Desktop implementation of [StorageAdapter] */
class DesktopStorageAdapter: StorageAdapter, JavaContext() {

    private val dataPath = Paths.get(System.getProperty("user.home"), dataDir)
    private val imagesPath = dataPath.resolve(imagesDir)
    private val thumbnailPath = dataPath.resolve(thumbnailDir)
    private val couchbasePath = dataPath.resolve(couchbaseDir)!!

    private val imageCache = LRUCacheMap<String, ByteArray>(1000)
    private val thumbnailCache = LRUCacheMap<String, ByteArray>(1000)

    init {
        if (!Files.exists(dataPath)) Files.createDirectories(dataPath)
        if (!Files.exists(couchbasePath)) Files.createDirectories(couchbasePath)
        if (!Files.exists(imagesPath)) Files.createDirectories(imagesPath)
        if (!Files.exists(thumbnailPath)) Files.createDirectories(thumbnailPath)
    }

    /** Save image from [InputStream] to Storage */
    suspend override fun saveImage(image: InputStream): String {
        val id = generateUniqueID()
        writeImageToPath(image, imagesPath.resolve(id))
        return id
    }

    /** Delete image from storage */
    suspend override fun deleteImage(id: String) {
        imageCache.remove(id)
        thumbnailCache.remove(id)
        thumbnailPath.resolve(id)
                .takeIf { Files.exists(it) }
                ?.also { Files.delete(it) }
        imagesPath.resolve(id)
                .takeIf { Files.exists(it) }
                ?.also { Files.delete(it) }
                ?: throw StorageAdapter.StorageException.EntityDoesNotExist(id)
    }

    /** Get thumbnail of image with [id] */
    suspend override fun getThumbnail(id: String): InputStream = if (thumbnailCache.containsKey(id)) {
        thumbnailCache[id]!!.inputStream()
    } else {
        if (Files.exists(thumbnailPath.resolve(id))) {
            val byteArray = Files.newInputStream(thumbnailPath.resolve(id)).readBytes()
            thumbnailCache.put(id, byteArray)
            byteArray.inputStream()
        } else {
            val outStream = ByteArrayOutputStream()
            Thumbnails.of(getImage(id))
                    .size(100, 100)
                    .outputFormat("jpeg")
                    .toOutputStream(outStream)
            val byteArray = outStream.toByteArray()
            Files.write(thumbnailPath.resolve(id), byteArray)
            thumbnailCache.put(id, byteArray)
            byteArray.inputStream()
        }
    }

    /** Write [image] stream to [destinationPath] with given [id] */
    private suspend fun writeImageToPath(image: InputStream, destinationPath: Path)
            = Files.copy(image, destinationPath, StandardCopyOption.REPLACE_EXISTING)

    /** Generate random image id that does not exist in [imagesDir]*/
    private suspend fun generateUniqueID(): String {
        var id = UUID.randomUUID().toString()
        var path = imagesPath.resolve(id)
        while (Files.exists(path)) {
            id = UUID.randomUUID().toString()
            path = imagesPath.resolve(id)
        }
        return id
    }

    /** Get image as [InputStream] from storage */
    suspend override fun getImage(id: String): InputStream = if (imageCache.containsKey(id)) {
        imageCache[id]!!.inputStream()
    } else {
        val fileByteArray = (imagesPath.resolve(id)
                .takeIf { Files.exists(it) }
                ?.let { Files.newInputStream(it) }
                ?: throw StorageAdapter.StorageException.EntityDoesNotExist(id)).readBytes()
        imageCache.put(id, fileByteArray)
        fileByteArray.inputStream()
    }

    /** See [JavaContext]. Overridden to modify couchbase path to central storage */
    override fun getFilesDir(): File = couchbasePath.toFile()


    companion object {
        private const val dataDir = ".paiman"
        private const val couchbaseDir = "couchbase"
        private const val imagesDir = "images"
        private const val thumbnailDir = "thumbnails"
    }

}