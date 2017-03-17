package de.x4fyr.paiman.lib.domain

import java.time.LocalDate


/**
 * Selling information container.
 */
data class SellingInformation(val purchaser: Purchaser, val date: LocalDate?, val price: Double = 0.0)