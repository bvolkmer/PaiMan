package de.x4fyr.paiman.app.ui.sell

import de.x4fyr.paiman.lib.domain.Purchaser
import de.x4fyr.paiman.lib.domain.SellingInformation
import javafx.beans.property.*
import tornadofx.*
import java.time.LocalDate

/**
 * Created by x4fyr on 3/23/17.
 */
class Model(sellingInformation: SellingInformation) {
    val name = SimpleStringProperty(sellingInformation.purchaser.name)
    val address = SimpleStringProperty(sellingInformation.purchaser.address)
    val price = SimpleDoubleProperty(sellingInformation.price)
    val date = SimpleObjectProperty<LocalDate>(sellingInformation.date)
}