package de.x4fyr.paiman.app.services

import de.x4fyr.paiman.app.ui.Controller
import de.x4fyr.paiman.app.ui.forEach
import de.x4fyr.paiman.app.ui.produceString
import javafx.concurrent.Worker
import javafx.scene.web.WebView
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import netscape.javascript.JSException
import netscape.javascript.JSObject
import org.w3c.dom.Element
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Singleton

/** JavaFx implementation of [WebViewService] */
@Singleton
class JavaFxWebViewService: WebViewService {
    /** Invoke javascript on webview */
    override fun invoke(command: String) = webView.engine.executeScript(command) as JSObject?

    /** [WebView] instance */
    val webView: WebView = WebView()

    private val debugPath = Files.createTempDirectory("paiman_webview_debug_")
    private val debugCssPath = Files.createDirectory(debugPath.resolve("css"))
    private val debugJsPath = Files.createDirectory(debugPath.resolve("js"))
    private val debugIndex: Path = debugPath.resolve("index.html")

    init {
        println("Writing debug html to ${debugIndex.toAbsolutePath()}")
        //Hide scrollbars
        webView.engine.loadWorker.stateProperty().addListener { _, _, newState ->
            if (newState == Worker.State.SUCCEEDED) {
                try {
                    webView.engine.executeScript("document.style.overlow = 'hidden';")
                } catch (e: JSException) { //Ignore "undefined is not an object" TypeError
                }
            }
        }
    }


    override fun loadUI(htmlElement: Element) {
        launch(JavaFx) {
            Files.write(debugIndex, htmlElement.produceString().toByteArray())
            //Replace css references
            htmlElement.getElementsByTagName("link").forEach {
                if (it is Element) {
                    if (it.getAttribute("rel") == "stylesheet") {
                        var resourceStr = it.getAttribute("href")
                        if (resourceStr.startsWith("./")) {
                            resourceStr = resourceStr.drop(".".length)
                            it.setAttribute("href", this.javaClass.getResource(resourceStr)?.toExternalForm() ?: throw
                            RuntimeException("Resource $resourceStr not found"))
                            Files.write(debugCssPath.resolve(resourceStr.drop("/css/".length)),
                                    this.javaClass.getResourceAsStream(resourceStr).readBytes())
                        }
                    }
                }
            }
            //Replace js references
            htmlElement.getElementsByTagName("script").forEach {
                if (it is Element) {
                    if (it.hasAttribute("src")) {
                        var resourceStr = it.getAttribute("src")
                        if (resourceStr.startsWith("./")) {
                            resourceStr = resourceStr.drop(".".length)
                            it.setAttribute("src", this.javaClass.getResource(resourceStr)?.toExternalForm() ?: throw
                            RuntimeException("Resource $resourceStr not found"))
                            Files.write(debugJsPath.resolve(resourceStr.drop("/js/".length)),
                                    this.javaClass.getResourceAsStream(resourceStr).readBytes())
                        }
                    }
                }
            }
            webView.engine.loadContent(htmlElement.produceString())
        }
    }

    override fun setCallbackController(controller: Controller) {
        launch(JavaFx) {
            webView.engine.loadWorker.stateProperty().addListener { _, _, newValue ->
                if (newValue == Worker.State.SUCCEEDED) {
                    (webView.engine.executeScript("window") as JSObject)
                            .removeMember(WebViewService.javascriptModuleName)
                    (webView.engine.executeScript("window") as JSObject)
                            .setMember(WebViewService.javascriptModuleName, controller)
                }
            }
        }
    }
}