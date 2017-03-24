package de.x4fyr.paiman.app.ui.sell

import com.jfoenix.controls.JFXButton
import de.x4fyr.paiman.app.ui.detail.DetailView
import javafx.scene.Parent
import javafx.scene.control.TextFormatter
import javafx.util.converter.DoubleStringConverter
import javafx.util.converter.NumberStringConverter
import tornadofx.*

/**
 * Created by x4fyr on 3/23/17.
 */
class View : tornadofx.View() {
    val controller by inject<Controller>()
    val model by param<Model>()
    val referrer by param<DetailView>()
    val paintingId by param<String>()

    override val root = vbox {
        this += JFXButton("<-").apply { setOnAction { replaceWith(referrer) } }
        form {
            fieldset("Sell painting") {
                field("Seller Name") {
                    textfield { bind(model.name) }
                }
                field("Seller address") {
                    textfield { bind(model.address) }
                }
                field("Price") {
                    textfield {
                        bind(property = model.price)
                        textFormatter = TextFormatter(NumberStringConverter())
                        properties += "vkType" to "numeric"
                    }
                }
                field("Date") {
                    datepicker { bind(property = model.date) }
                }
                this += JFXButton("Commit").apply {
                    setOnAction {
                        controller.commit()
                    }
                }
            }
        }
    }
}