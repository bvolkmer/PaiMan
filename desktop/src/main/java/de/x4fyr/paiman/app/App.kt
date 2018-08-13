package de.x4fyr.paiman.app

import de.x4fyr.paiman.app.services.JavaFxWebViewService
import de.x4fyr.paiman.app.ui.views.entry.EntryController
import de.x4fyr.paiman.dagger.DaggerMainComponent
import de.x4fyr.paiman.dagger.DesktopServiceModule
import de.x4fyr.paiman.dagger.MainComponent
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch

/** Html ui wrapper application */
class App: Application() {

    private lateinit var webViewService: JavaFxWebViewService
    private lateinit var entryController: EntryController

    /** See [Application.start] */
    override fun start(primaryStage: Stage) {
        val component: MainComponent = DaggerMainComponent.builder()
                .desktopServiceModule(DesktopServiceModule(primaryStage))
                .build()
        webViewService = component.webViewServiceImpl()
        entryController = component.entryController()

        launch(JavaFx) {
            entryController.loadView()
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
