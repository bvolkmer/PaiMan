package de.x4fyr.paiman.lib.domain

import org.threeten.bp.LocalDate


/**
 * Selling information container.
 */
data class SellingInformation(var purchaser: Purchaser = Purchaser("", ""),
                              var date: LocalDate = LocalDate.now(),
                              var price: Double = 0.0)