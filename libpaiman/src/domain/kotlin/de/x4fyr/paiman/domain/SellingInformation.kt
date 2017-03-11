package de.x4fyr.paiman.domain

import de.x4fyr.paiman.domain.dateTime.LocalDate


/**
 * Selling information container.
 */
data class SellingInformation(val purchaser: Purchaser, val date: LocalDate?, val price: Double = 0.0)