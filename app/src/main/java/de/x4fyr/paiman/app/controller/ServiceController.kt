package de.x4fyr.paiman.app.controller

import de.x4fyr.paiman.lib.provider.PictureProvider
import de.x4fyr.paiman.lib.provider.ServiceProvider
import de.x4fyr.paiman.lib.services.PaintingService
import de.x4fyr.paiman.lib.services.QueryService
import tornadofx.*
import java.io.File
import java.io.InputStream
import java.util.*
import java.util.logging.Logger

/**
 * Controller managing services through provider
 */
class ServiceController : Controller() {

    private val LOG = Logger.getLogger(this::class.simpleName)
    /** Platform dependent ServiceProvider instance */
    val serviceProvider by lazy { loadProvider<ServiceProvider>(ServiceProvider::class.java) }
    /** Platform dependent PictureProvider instance */
    val pictureProvider by lazy { loadProvider<PictureProvider>(PictureProvider::class.java) }

    /** PaintingService instance */
    val paintingService: PaintingService by lazy { serviceProvider.paintingService }
    /** QueryService instance */
    val queryService: QueryService by lazy { serviceProvider.queryService }


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

    /** Open a given path as an InputStream
     * @param path path to open
     */
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