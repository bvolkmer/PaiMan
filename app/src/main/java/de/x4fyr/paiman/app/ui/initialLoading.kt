package de.x4fyr.paiman.app.ui

import de.x4fyr.paiman.app.controller.ServiceController
import de.x4fyr.paiman.app.utils.jfxSpinner
import de.x4fyr.paiman.lib.provider.PictureProvider
import de.x4fyr.paiman.lib.provider.ServiceProvider
import javafx.stage.Screen
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import tornadofx.*

/**
 * Controller for initial loading view
 */
class initialLoadingController : Controller() {

    private val view: InitialLoadingView by inject()
    private val serviceController: ServiceController by inject()
    private lateinit var serviceProvider: ServiceProvider
    private lateinit var pictureProvider: PictureProvider

    /** Initialising actions*/
    fun init() {
        launch(JavaFx) {
            async(JavaFx) {
                serviceProvider = serviceController.serviceProvider
                pictureProvider = serviceController.pictureProvider
            }
        }.invokeOnCompletion {
            view.replaceWith(find<MainView>())
        }
    }
}

/**
 * View shown while the app is loading
 */
class InitialLoadingView : View() {
    private val controller: initialLoadingController by inject()
    private val bounds = Screen.getPrimary().visualBounds!!

    /**  */
    override val root = stackpane {
        maxHeight = bounds.height
        maxWidth = bounds.width
        prefHeight = bounds.height
        prefWidth = bounds.width
        jfxSpinner()
    }

    /**  */
    override fun onDock() {
        controller.init()
    }

}