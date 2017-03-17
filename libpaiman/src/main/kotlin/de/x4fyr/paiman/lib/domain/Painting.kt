package de.x4fyr.paiman.lib.domain

import de.x4fyr.paiman.lib.domain.Picture
import java.time.LocalDate


/**
 * Painting container.
 * A painting is a collection of pictures and other information all belonging to one and the same painting.
 */
data class Painting(val id: Long? = null,
                    val mainPicture: Picture,
                    val wip: Set<Picture> = setOf(),
                    val references: Set<Picture> = setOf(),
                    val finishingDate: LocalDate? = null,
                    val sellingInfo: SellingInformation?,
                    val tags: Set<String> = setOf(),
                    val finished: Boolean = false)

