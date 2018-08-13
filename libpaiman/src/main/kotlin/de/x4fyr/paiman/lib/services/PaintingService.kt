package de.x4fyr.paiman.lib.services

import com.couchbase.lite.QueryEnumerator
import de.x4fyr.paiman.lib.domain.Picture
import de.x4fyr.paiman.lib.domain.Purchaser
import de.x4fyr.paiman.lib.domain.SavedPainting
import de.x4fyr.paiman.lib.domain.SellingInformation
import org.threeten.bp.LocalDate
import java.io.InputStream

/**
 * Service for querying, creating, removing and manipulating paintings, including the needed persistence actions
 */
interface PaintingService {


    /**
     * Compose a new painting
     */
    @Throws(ServiceException::class)
    suspend fun composeNewPainting(title: String,
                           mainPicture: InputStream,
                           wip: Set<InputStream> = setOf(),
                           reference: Set<InputStream> = setOf(),
                           sellingInfo: SellingInformation? = null,
                           tags: Set<String> = setOf()): SavedPainting

    /** Change a SavedPainting determined by the given id and probably changed properties */
    @Throws(ServiceException::class)
    suspend fun changePainting(painting: SavedPainting): SavedPainting

    /** Replace the main picture of a painting optionally moving the old to wip */
    @Throws(ServiceException::class)
    suspend fun replaceMainPicture(painting: SavedPainting, newPicture: InputStream, moveOldToWip: Boolean):
    SavedPainting

    /** Add selling information to a painting */
    @Throws(ServiceException::class)
    suspend fun sellPainting(painting: SavedPainting, purchaser: Purchaser, date: LocalDate, price: Double):
    SavedPainting

    /** Add wip pictures from given InputStreams */
    @Throws(ServiceException::class)
    suspend fun addWipPicture(painting: SavedPainting, images: Set<InputStream>): SavedPainting

    /** Remove wip pictures from given ids */
    @Throws(ServiceException::class)
    suspend fun removeWipPicture(painting: SavedPainting, images: Set<String>): SavedPainting

    /** Add reference pictures from given InputStreams */
    @Throws(ServiceException::class)
    suspend fun addReferences(painting: SavedPainting, references: Set<InputStream>): SavedPainting

    /** Remove reference pictures from given ids */
    @Throws(ServiceException::class)
    suspend fun removeReferences(painting: SavedPainting, references: Set<String>): SavedPainting

    /** Add tags */
    @Throws(ServiceException::class)
    suspend fun addTags(painting: SavedPainting, tags: Set<String>): SavedPainting

    /** Remove tags */
    @Throws(ServiceException::class)
    suspend fun removeTags(painting: SavedPainting, tags: Set<String>): SavedPainting

    /** Get a SavedPainting by id */
    @Throws(ServiceException::class)
    suspend fun get(id: String): SavedPainting

    /** Get multiple SavedPaintings by given ids */
    @Throws(ServiceException::class)
    suspend fun getAll(ids: Set<String>): Set<SavedPainting>

    /** Get Paintings from Query results */
    @Throws(ServiceException::class)
    suspend fun getFromQueryResult(queryEnumerator: QueryEnumerator): Set<SavedPainting>

    /** Delete a SavedPainting */
    @Throws(ServiceException::class)
    suspend fun delete(painting: SavedPainting)

    /** Delete a paining by id */
    @Throws(ServiceException::class)
    suspend fun delete(paintingId: String)

    /** Get a InputStream of a picture belonging to a given painting */
    @Throws(ServiceException::class)
    suspend fun getPictureStream(picture: Picture): InputStream

    /** Get a InputStream of a thumbnail of a picture */
    @Throws(ServiceException::class)
    suspend fun getPictureThumbnailStream(picture: Picture): InputStream
}
