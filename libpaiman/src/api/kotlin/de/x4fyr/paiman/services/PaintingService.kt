package de.x4fyr.paiman.services

import de.x4fyr.paiman.domain.Painting
import de.x4fyr.paiman.domain.Picture
import de.x4fyr.paiman.domain.Purchaser
import de.x4fyr.paiman.domain.SellingInformation
import java.time.LocalDate

/**
 * Service for querying, creating, removing and manipulating paintings.
 */
interface PaintingService {

    fun getAll(): Set<Painting>

    fun getById(id: Int): Painting

    fun getByTag(tag: String): Set<Painting>

    fun getByTagsOr(tags: Set<String>): Set<Painting>

    fun getByTagsAnd(tags: Set<String>): Set<Painting>

    fun getByPurchaser(purchaser: Purchaser): Set<Painting>

    fun getByDate(date: LocalDate): Set<Painting>

    fun remove(painting: Painting)

    fun createNewPainting(mainImage: Picture,
                          wip: Set<Picture>?,
                          reference: Set<Picture>?,
                          sellingInfo: SellingInformation?,
                          tags: Set<String>): Painting

    fun replaceMainPicture(painting: Painting, newPicture: Picture, moveOldToWip: Boolean = true): Painting

    fun sellPainting(painting: Painting,
                     purchaser: Purchaser,
                     date: LocalDate? = LocalDate.now(),
                     price: Long = 0): Painting

    fun addWipImages(painting: Painting, images: Set<Picture>): Painting

    fun addReferences(painting: Painting, references: Set<Picture>): Painting

    fun replaceTags(painting: Painting, tags: Set<String>): Painting

}
