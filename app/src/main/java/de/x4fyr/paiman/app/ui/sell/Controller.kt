package de.x4fyr.paiman.app.ui.sell

import de.x4fyr.paiman.app.controller.ServiceController
import de.x4fyr.paiman.lib.domain.Purchaser
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import java.util.logging.Logger

/**
 * Created by x4fyr on 3/23/17.
 */
class Controller : tornadofx.Controller() {
    val view by inject<View>()
    val serviceController by inject<ServiceController>()
    val paintingService = serviceController.paintingService
    val LOG = Logger.getLogger(this::class.simpleName)

    fun commit() {
        launch(JavaFx) {
            with(view) {
                with(model) {
                    val painting = paintingService.get(paintingId)
                    paintingService.sellPainting(painting = painting, date = date.value, price = price.value,
                            purchaser = Purchaser(name = name.value, address = address.value))
                }
                replaceWith(referrer)
            }
        }
    }
}