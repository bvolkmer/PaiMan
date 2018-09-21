package de.x4fyr.paiman.app.services

import de.x4fyr.paiman.app.ui.Controller
import de.x4fyr.paiman.app.ui.Model
import de.x4fyr.paiman.app.ui.produceString
import javafx.concurrent.Worker
import javafx.scene.web.WebView
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import netscape.javascript.JSException
import netscape.javascript.JSObject
import org.w3c.dom.Element
import javax.inject.Singleton

/** JavaFx implementation of [WebViewService] */
@Singleton
class JavaFxWebViewService : WebViewService {

    /** [WebView] instance */
    val webView: WebView = WebView()

    init {
        //Hide scrollbars
        webView.engine.loadWorker.stateProperty().addListener { _, _, newState ->
            if (newState == Worker.State.SUCCEEDED) {
                try {
                    webView.engine.executeScript("document.style.overlow = 'hidden';")
                } catch (e: JSException) { //Ignore "undefined is not an object" TypeError
                }
            }
        }
        webView.engine.documentProperty().addListener { _, _, _ ->
            webView.engine.executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
            (webView.engine.executeScript("window") as JSObject).setMember("javaDebug", Debug())
        }
    }


    override fun loadUI(htmlElement: Element) {
        launch(JavaFx) {
            webView.engine.loadContent(htmlElement.produceString())
        }
    }

    override fun loadHtml(html: String, controller: Controller, model: Model?) {
        launch(JavaFx) {
            val url = this.javaClass.getResource("/view/$html")
            webView.engine.load(url.toExternalForm())
            //TODO: Reload only when visible
            setControllerAndModel(controller, model)
        }
    }

    override fun setControllerAndModel(controller: Controller, model: Model?) {
        launch(JavaFx) {
            webView.engine.loadWorker.stateProperty().addListener { _, _, newValue ->
                if (newValue == Worker.State.SUCCEEDED) {
                    (webView.engine.executeScript("window") as JSObject)
                            .removeMember(WebViewService.javascriptControllerModuleName)
                    (webView.engine.executeScript("window") as JSObject)
                            .setMember(WebViewService.javascriptControllerModuleName, controller)
                    (webView.engine.executeScript("window") as JSObject)
                            .removeMember(WebViewService.javascriptModelModuleName)
                    if (model != null) (webView.engine.executeScript("window") as JSObject)
                            .setMember(WebViewService.javascriptModelModuleName, model)
                }
            }
        }
    }

    open class Debug {
        open fun log(text: String){
            text.lines().forEach {
                println("Javascript log: $it")
            }
        }
    }
}