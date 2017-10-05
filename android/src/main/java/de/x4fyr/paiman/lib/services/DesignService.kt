package de.x4fyr.paiman.lib.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import de.x4fyr.paiman.R
import de.x4fyr.paiman.app.BitmapCache
import de.x4fyr.paiman.lib.domain.Picture
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.withLock

/**
 * Service serving design information and actions
 */
@Singleton
class DesignService @Inject constructor(context: Context, private var paintingService: PaintingService) {
    /** Whether this device should be considered large, i.e. a tablet */
    val isLargeDevice: Boolean = context.resources.getBoolean(R.bool.large_layout)

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
    suspend fun getOrLoadFullSizeBitmap(picture: Picture): Bitmap {
        // access
        Log.d("${this::class.simpleName}::getFullSize", "Getting ${picture.id}")
        return fullSizeLock.withLock {
            Log.d("${this::class.simpleName}::getFullSize", "Acquired lock for ${picture.id}")
            val cached = fullSizeCache[picture.id]
            (if (cached != null) cached
            else {
                val stream = paintingService.getPictureStream(picture)
                BitmapFactory.decodeStream(stream).also {
                    stream.close()
                    fullSizeCache.put(picture.id, it)
                }
            }).also {
                Log.d("${this::class.simpleName}::getFullSize", "Got ${picture.id}")
            }
        }
    }

    /** Get thumbnail scaled [Bitmap] of [Picture] from cache or load it if missing  */
    suspend fun getOrLoadThumbnailBitmap(picture: Picture): Bitmap {
        Log.d("${this::class.simpleName}::getThumbnail", "Getting ${picture.id}")
        return thumbnailLock.withLock {
            Log.d("${this::class.simpleName}::getThumbnail", "Acquired lock for ${picture.id}")
            val cached = thumbnailCache[picture.id]
            (if (cached != null) cached
            else {
                val fullSize = getOrLoadFullSizeBitmap(picture = picture)
                Bitmap.createScaledBitmap(fullSize, 100, 100, false)
            }).also {
                Log.d("${this::class.simpleName}::getThumbnail", "Got ${picture.id}")
            }
        }

    }

}