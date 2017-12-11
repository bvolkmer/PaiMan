package de.x4fyr.paiman.app.services

import de.x4fyr.paiman.app.ui.controller.Controller
import javafx.concurrent.Worker
import javafx.scene.web.WebView
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import netscape.javascript.JSException
import netscape.javascript.JSObject
import javax.inject.Singleton

@Singleton
class WebViewServiceImpl: WebViewService {

    val webView: WebView = WebView()

    init {
        //Hide scrollbars
        webView.engine.loadWorker.stateProperty().addListener { _, _, newState ->
            if (newState == Worker.State.SUCCEEDED) {
                try {
                    webView.engine.executeScript("document.style.overlow = 'hidden';")
                } catch (e: JSException) { //Ignore "undefined is not an oobject" TypeError
                }
            }
        }
    }


    override fun loadUI(appendable: Appendable) {
        launch(JavaFx) {
            println(appendable.toString())
            webView.engine.loadContent(appendable.toString())
        }
    }

    override fun setCallbackController(controller: Controller) {
        launch(JavaFx) {
            (webView.engine.executeScript("window") as JSObject).setMember("controller", controller)
        }
    }
}