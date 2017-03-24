package de.x4fyr.paiman.lib.services

import com.couchbase.lite.QueryEnumerator
import de.x4fyr.paiman.lib.domain.*
import java.io.InputStream
import java.time.LocalDate

/**
 * Service for querying, creating, removing and manipulating paintings.
 */
interface PaintingService {


    @Throws(ServiceException::class)
    fun composeNewPainting(title: String,
                           mainPicture: InputStream,
                           wip: Set<InputStream> = setOf(),
                           reference: Set<InputStream> = setOf(),
                           sellingInfo: SellingInformation? = null,
                           tags: Set<String> = setOf()): SavedPainting
    @Throws(ServiceException::class)
    fun changePainting(painting: SavedPainting): SavedPainting
    @Throws(ServiceException::class)
    fun replaceMainPicture(painting: SavedPainting, newPicture: InputStream, moveOldToWip: Boolean): SavedPainting
    @Throws(ServiceException::class)
    fun sellPainting(painting: SavedPainting, purchaser: Purchaser, date: LocalDate, price: Double): SavedPainting
    @Throws(ServiceException::class)
    fun addWipPicture(painting: SavedPainting, images: Set<InputStream>): SavedPainting
    @Throws(ServiceException::class)
    fun removeWipPicture(painting: SavedPainting, images: Set<String>): SavedPainting
    @Throws(ServiceException::class)
    fun addReferences(painting: SavedPainting, references: Set<InputStream>): SavedPainting
    @Throws(ServiceException::class)
    fun removeReferences(painting: SavedPainting, references: Set<String>): SavedPainting
    @Throws(ServiceException::class)
    fun addTags(painting: SavedPainting, tags: Set<String>): SavedPainting
    @Throws(ServiceException::class)
    fun removeTags(painting: SavedPainting, tags: Set<String>): SavedPainting
    @Throws(ServiceException::class)
    fun get(id: String): SavedPainting
    @Throws(ServiceException::class)
    fun getAll(ids: Set<String>): Set<SavedPainting>
    @Throws(ServiceException::class)
    fun getPictureStream(picture: Picture, painting: SavedPainting): InputStream
    @Throws(ServiceException::class)
    fun getAllPictureStreams(pictures: Set<Picture>, painting: SavedPainting): Set<InputStream>
    fun getFromQueryResult(queryEnumerator: QueryEnumerator): Set<SavedPainting>
}
