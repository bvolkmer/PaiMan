package de.x4fyr.paiman.lib.services

import de.x4fyr.paiman.lib.domain.Painting
import de.x4fyr.paiman.lib.domain.Picture
import de.x4fyr.paiman.lib.domain.Purchaser
import de.x4fyr.paiman.lib.domain.SellingInformation
import java.time.LocalDate

/**
 * Service for querying, creating, removing and manipulating paintings.
 */
interface PaintingService {


    fun composeNewPainting(mainPicture: Picture,
                           wip: Set<Picture>,
                           reference: Set<Picture>,
                           sellingInfo: SellingInformation,
                           tags: Set<String>): Painting

    fun replaceMainPicture(painting: Painting, newPicture: Picture, moveOldToWip: Boolean): Painting

    fun sellPainting(painting: Painting, purchaser: Purchaser, date: LocalDate?, price: Double): Painting

    fun addWipPicture(painting: Painting, images: Set<Picture>): Painting

    fun removeWipPicture(painting: Painting, images: Set<Picture>): Painting

    fun addReferences(painting: Painting, references: Set<Picture>): Painting

    fun removeReferences(painting: Painting, references: Set<Picture>): Painting

    fun addTags(painting: Painting, tags: Set<String>): Painting

    fun removeTags(painting: Painting, tags: Set<String>): Painting

}
