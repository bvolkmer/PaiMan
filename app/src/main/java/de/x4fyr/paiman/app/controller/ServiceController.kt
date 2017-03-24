package de.x4fyr.paiman.app.controller

import de.x4fyr.paiman.lib.provider.PictureProvider
import de.x4fyr.paiman.lib.provider.ServiceProvider
import de.x4fyr.paiman.lib.services.PaintingService
import de.x4fyr.paiman.lib.services.QueryService
import tornadofx.*
import java.io.File
import java.io.InputStream
import java.util.ServiceLoader
import java.util.logging.Logger
import kotlin.reflect.full.isSubclassOf

/**
 * Created by x4fyr on 3/20/17.
 */
class ServiceController : Controller() {

    val LOG = Logger.getLogger(this::class.simpleName)
    val serviceProvider = loadProvider<ServiceProvider>(ServiceProvider::class.java)
    val pictureProvider = loadProvider<PictureProvider>(PictureProvider::class.java)

    val paintingService: PaintingService = serviceProvider.paintingService
    val queryService: QueryService = serviceProvider.queryService


    private fun <T : Any> loadProvider(clazz: Class<T>): T {
        var result: T? = null
        val serviceLoader: ServiceLoader<out T> = ServiceLoader.load(clazz)
        for (provider in serviceLoader.iterator()) {
            if (result == null) {
                result = provider
            } else {
                break
            }
        }
        return result ?: throw RuntimeException("No ServiceProvider found!")
    }

    fun pathToInputStream(path: String): InputStream? {
        val pictureFile: File = File(path)
        if (pictureFile.exists() && pictureFile.canRead()) {
            val inputStream = pictureFile.inputStream()
            if (inputStream.available() > 0) return inputStream
            else LOG.warning("Trying to read empty file: ${pictureFile.path}")
        } else LOG.warning(
                "Error: ${pictureFile.path} -> exists:${pictureFile.exists()} || canRead:${pictureFile.canRead()}")

        return null
    }
}