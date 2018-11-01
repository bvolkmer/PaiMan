package de.x4fyr.paiman.app

import de.x4fyr.paiman.app.services.JavaFxWebViewService
import de.x4fyr.paiman.app.ui.views.entry.EntryController
import de.x4fyr.paiman.dagger.DaggerMainComponent
import de.x4fyr.paiman.dagger.MainComponent
import de.x4fyr.paiman.dagger.desktop.DesktopServiceModule
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch

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

        GlobalScope.launch(Dispatchers.JavaFx) {
            entryController.loadView()
            val stackPane = StackPane(webViewService.webView)
            val scene = Scene(stackPane)
            primaryStage.scene = scene
            primaryStage.show()

            primaryStage.onCloseRequest = javafx.event.EventHandler {
                Platform.exit()
                System.exit(0)
            }

        }
    }

}

/** Main entry point */
fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}
