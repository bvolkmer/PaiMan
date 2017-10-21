package de.x4fyr.paiman.lib.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import de.x4fyr.paiman.R
import de.x4fyr.paiman.app.BitmapCache
import de.x4fyr.paiman.lib.adapter.StorageAdapter
import de.x4fyr.paiman.lib.domain.Picture
import java.util.concurrent.ExecutionException
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.withLock

/**
 * Service serving design information and actions
 */
@Singleton
class DesignService @Inject constructor(private val context: Context, private var paintingService: PaintingService) {

    private val fullSizeCache: BitmapCache<String>
    private val thumbnailCache: BitmapCache<String>

    private val fullSizeLock = ReentrantLock()
    private val thumbnailLock = ReentrantLock()

    init {
        val cacheSize = (Runtime.getRuntime().maxMemory()/1024/8).toInt()
        fullSizeCache = BitmapCache(cacheSize)
        thumbnailCache = BitmapCache(cacheSize)
    }

    /** Get [Bitmap] of [Picture] from cache or load it if missing  */
    @Throws(ServiceException::class)
    suspend fun getOrLoadFullSizeBitmap(picture: Picture): Bitmap =
            fullSizeLock.withLock {
                fullSizeCache[picture.id] ?: try {
                    val result = paintingService.getPictureStream(picture).get()
                    result.use {
                        BitmapFactory.decodeStream(it)!!.also {
                            fullSizeCache.put(picture.id, it)
                        }
                    }
                } catch (e: ExecutionException) {
                    if (e.cause is StorageAdapter.StorageException.EntityDoesNotExist) {
                        BitmapFactory.decodeResource(context.resources, R.drawable.ic_broken_image_black_24dp)
                    } else throw e.cause ?: e
                }
            }

    /** Get thumbnail scaled [Bitmap] of [Picture] from cache or load it if missing  */
    @Throws(ServiceException::class)
    suspend fun getOrLoadThumbnailBitmap(picture: Picture): Bitmap =
            thumbnailLock.withLock {
                thumbnailCache[picture.id] ?: run {
                    val fullSizeBitmap = getOrLoadFullSizeBitmap(picture)
                    if (fullSizeBitmap.width > 100 || fullSizeBitmap.height > 100) {
                        Bitmap.createScaledBitmap(
                                getOrLoadFullSizeBitmap(picture = picture), 100, 100, false).also {
                            thumbnailCache.put(picture.id, it)
                        }
                    } else fullSizeBitmap
                }
            }
}