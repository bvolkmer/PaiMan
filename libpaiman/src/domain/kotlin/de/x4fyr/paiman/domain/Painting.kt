package de.x4fyr.paiman.domain

import java.time.LocalDate

/**
 * Painting container.
 * A painting is a collection of pictures and other information all belonging to one and the same painting.
 */
data class Painting(val id: Int,
                    val mainImage: Picture,
                    val wip: Set<Picture> = setOf(),
                    val references: Set<Picture> = setOf(),
                    val finishingDate: LocalDate?,
                    val sellingInfo: SellingInformation?,
                    val tags: Set<String> = setOf(),
                    val finished: Boolean = false)

