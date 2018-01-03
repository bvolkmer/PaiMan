package de.x4fyr.paiman.app

import de.x4fyr.paiman.app.dagger.MainComponent
import de.x4fyr.paiman.app.services.JavaFxWebViewService
import de.x4fyr.paiman.app.ui.controller.EntryViewController
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch

/** Html ui wrapper application */
class App: Application() {

    private lateinit var webViewService: JavaFxWebViewService
    private lateinit var entryViewController: EntryViewController

    /** See [Application.start] */
    override fun start(primaryStage: Stage) {
        val component: MainComponent = DaggerMainComponent.create()
        webViewService = component.webViewServiceImpl()
        entryViewController = component.entryUIController()

        launch(JavaFx) {
            entryViewController.loadView()
            val stackPane = StackPane(webViewService.webView)
            val scene = Scene(stackPane)
            primaryStage.scene = scene
            primaryStage.show()
        }
    }

}

/** Main entry point */
fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}
