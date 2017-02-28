package de.x4fyr.paiman.domain

import java.time.LocalDate

/**
 * Selling information container.
 */
data class SellingInformation(val purchaser: Purchaser, val date: LocalDate?, val price: Long)