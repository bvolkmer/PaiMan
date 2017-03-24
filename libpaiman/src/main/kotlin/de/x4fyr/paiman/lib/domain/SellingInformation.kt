package de.x4fyr.paiman.lib.domain

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import java.time.LocalDate


/**
 * Selling information container.
 */
data class SellingInformation(var purchaser: Purchaser = Purchaser("", ""),
                              var date: LocalDate = LocalDate.now(),
                              var price: Double = 0.0)