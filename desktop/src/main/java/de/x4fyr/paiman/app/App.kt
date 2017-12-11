package de.x4fyr.paiman.app

import de.x4fyr.paiman.app.dagger.DaggerMainComponent
import de.x4fyr.paiman.app.dagger.MainComponent
import de.x4fyr.paiman.app.services.WebViewServiceImpl
import de.x4fyr.paiman.app.ui.controller.EntryViewController
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch

/** Html ui wrapper application */
class App: Application() {

    private lateinit var webViewService: WebViewServiceImpl
    private lateinit var entryUIController: EntryViewController

    /** See [Application.start] */
    override fun start(primaryStage: Stage) {
        val component: MainComponent = DaggerMainComponent.create()
        webViewService = component.webViewServiceImpl()
        entryUIController = component.entryUIController()

        launch(JavaFx) {
            entryUIController.loadView()
            val stackPane = StackPane(webViewService.webView)
            val scene: Scene = Scene(stackPane)
            primaryStage.scene = scene
            primaryStage.show()
        }
    }

}

/** Main entry point */
fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}
