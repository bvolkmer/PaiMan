package de.x4fyr.paiman.lib.adapter

import com.couchbase.lite.JavaContext
import de.x4fyr.paiman.app.transform
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.UUID

/** Desktop implementation of [StorageAdapter] */
class DesktopStorageAdapter: StorageAdapter, JavaContext() {

    private val dataPath = Paths.get(System.getProperty("user.home"), dataDir)
    private val imagesPath = Paths.get(dataPath.toAbsolutePath().toString(), imagesDir)
    private val couchbasePath = Paths.get(dataPath.toAbsolutePath().toString(), couchbaseDir)!!

    init {
        if (!Files.exists(dataPath)) Files.createDirectories(dataPath)
        if (!Files.exists(couchbasePath)) Files.createDirectories(couchbasePath)
        if (!Files.exists(imagesPath)) Files.createDirectories(imagesPath)
    }

    /** Save image from [InputStream] to Storage */
    suspend override fun saveImage(image: InputStream): String {
        var id = UUID.randomUUID().toString()
        var path = imagesPath.resolve(id)
        while (Files.exists(path)) {
            id = UUID.randomUUID().toString()
            path = imagesPath.resolve(id)
        }
        Files.copy(image, path, StandardCopyOption.REPLACE_EXISTING)
        return id
    }

    /** Delete image from storage */
    suspend override fun deleteImage(id: String) {
        imagesPath.resolve(id).takeIf { Files.exists(it) }
                ?.also { Files.delete(it) }
                ?: throw StorageAdapter.StorageException.EntityDoesNotExist(id)
    }

    /** Get image as [InputStream] from storage */
    suspend override fun getImage(id: String): InputStream = imagesPath
            .resolve(id)
            .takeIf { Files.exists(it) }
            ?.transform { Files.newInputStream(it) }
            ?: throw StorageAdapter.StorageException.EntityDoesNotExist(id)

    /** See [JavaContext]. Overridden to modify couchbase path to central storage */
    override fun getFilesDir(): File = couchbasePath.toFile()


    companion object {
        private const val dataDir = ".paiman"
        private const val couchbaseDir = "couchbase"
        private const val imagesDir = "images"
    }

}