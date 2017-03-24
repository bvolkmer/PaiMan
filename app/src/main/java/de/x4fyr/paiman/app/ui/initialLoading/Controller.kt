package de.x4fyr.paiman.app.ui.initialLoading

import de.x4fyr.paiman.app.controller.ServiceController
import de.x4fyr.paiman.app.ui.initialLoading.View
import de.x4fyr.paiman.lib.services.PaintingService
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import tornadofx.*

/**
 * Created by x4fyr on 3/18/17.
 */
class Controller : tornadofx.Controller() {

    val view: View by inject()
    val serviceController: ServiceController by inject()
    lateinit var paintingService: PaintingService

    fun init() {
        launch(JavaFx) {
            paintingService = serviceController.paintingService
        }.invokeOnCompletion { view.loaded = true }
    }
}