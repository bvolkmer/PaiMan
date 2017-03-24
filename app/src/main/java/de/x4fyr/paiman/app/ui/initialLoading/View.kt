package de.x4fyr.paiman.app.ui.initialLoading

import com.jfoenix.controls.JFXSpinner
import de.x4fyr.paiman.app.ui.initialLoading.Controller
import de.x4fyr.paiman.app.ui.main.MainView
import javafx.geometry.Bounds
import javafx.geometry.Rectangle2D
import javafx.scene.Scene
import javafx.stage.Screen
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import tornadofx.*
import kotlin.properties.Delegates

/**
 * Created by x4fyr on 3/18/17.
 */
class View : tornadofx.View() {
    val controller: Controller by inject()
    val bounds = Screen.getPrimary().visualBounds!!

    var loaded: Boolean by Delegates.observable(false) {
        prop, old, new -> if (new) replaceWith(MainView::class)
    }

    override val root = stackpane {
        maxHeight = bounds.height
        maxWidth = bounds.width
        prefHeight = bounds.height
        prefWidth = bounds.width
        this += JFXSpinner()
    }

    init {
       controller.init()
    }

}
