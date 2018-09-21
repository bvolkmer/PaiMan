package de.x4fyr.paiman.app.service

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.webkit.WebView
import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.Controller
import de.x4fyr.paiman.app.ui.produceString
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.w3c.dom.Element

/** Android implementations of [WebViewService] */
class AndroidWebViewService(context: Context): WebViewService {

    /** WebView instance */
    val webView: WebView = WebView(context)

    init {
        launch(UI) {
            webView.settings.javaScriptEnabled = true
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }

    /** Load ui from content in [appendable] */
    override fun loadUI(htmlElement: Element) {
        launch(UI) {
            val html = htmlElement.produceString()
            val base64Encoded = android.util.Base64.encodeToString(html.toByteArray(), android.util.Base64.DEFAULT)
            Log.i(this@AndroidWebViewService::class.simpleName, html)
            webView.loadData(base64Encoded, "text/html; charset=utf-8", "base64")
        }
    }

    /** Set Controller for callbacks in ui ]
     *
     * Suppress JavascriptInterface because the interface itself never has annotated methods, but the implementations might have.
     */
    @SuppressLint("JavascriptInterface")
    override fun setCallbackController(controller: Controller) {
        launch(UI) {
            webView.removeJavascriptInterface(WebViewService.javascriptControllerModuleName)
            webView.addJavascriptInterface(controller, WebViewService.javascriptControllerModuleName)
        }
    }


}