package de.x4fyr.paiman.app

import de.x4fyr.paiman.app.dagger.MainComponent
import de.x4fyr.paiman.app.services.WebViewServiceImpl
import de.x4fyr.paiman.app.ui.controller.EntryViewController
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage

class App: Application() {

    lateinit var webViewService: WebViewServiceImpl
    lateinit var entryUIController: EntryViewController

    override fun start(primaryStage: Stage) {
        val component: MainComponent = DaggerMainComponent.create()
        webViewService = component.webViewServiceImpl()
        entryUIController = component.entryUIController()

        entryUIController.loadView()
        val stackPane = StackPane(webViewService.webView)
        val scene: Scene = Scene(stackPane)
        primaryStage.scene = scene
        primaryStage.show()
    }

}

fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}
