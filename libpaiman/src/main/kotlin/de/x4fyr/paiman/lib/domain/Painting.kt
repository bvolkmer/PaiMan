package de.x4fyr.paiman.lib.domain

import de.x4fyr.paiman.lib.domain.Picture
import java.time.LocalDate


/**
 * Painting container.
 * A painting is a collection of pictures and other information all belonging to one and the same painting.
 */
sealed class Painting(open val title: String,
                      open val mainPicture: Picture,
                      open val wip: Set<Picture> = setOf(),
                      open val references: Set<Picture> = setOf(),
                      open val finishingDate: LocalDate? = null,
                      open val sellingInfo: SellingInformation? = null,
                      open val tags: Set<String> = setOf(),
                      open val finished: Boolean = false)

data class UnsavedPainting(override val title: String,
                           override val wip: Set<Picture> = setOf(),
                           override val references: Set<Picture> = setOf(),
                           override val finishingDate: LocalDate? = null,
                           override val sellingInfo: SellingInformation? = null,
                           override val tags: Set<String> = setOf(),
                           override val mainPicture: Picture,
                           override val finished: Boolean = false) : Painting(title = title, mainPicture = mainPicture,
        wip = wip, references = references, finishingDate = finishingDate, sellingInfo = sellingInfo,
        tags = tags, finished = finished)

data class SavedPainting internal constructor(val id: String,
                         override val title: String,
                         override val wip: Set<Picture> = setOf(),
                         override val references: Set<Picture> = setOf(),
                         override val finishingDate: LocalDate? = null,
                         override val sellingInfo: SellingInformation? = null,
                         override val tags: Set<String> = setOf(),
                         override val mainPicture: Picture,
                         override val finished: Boolean = false) : Painting(title = title, mainPicture = mainPicture,
        wip = wip, references = references, finishingDate = finishingDate, sellingInfo = sellingInfo,
        tags = tags, finished = finished)



