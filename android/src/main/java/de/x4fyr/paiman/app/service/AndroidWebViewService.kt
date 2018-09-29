package de.x4fyr.paiman.app.service

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.Controller
import de.x4fyr.paiman.app.ui.Model
import de.x4fyr.paiman.app.ui.produceString
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.w3c.dom.Element
import kotlin.properties.Delegates

/** Android implementations of [WebViewService] */
class AndroidWebViewService(private val context: Context) : WebViewService {

    private val webViewClient = LoadStatusWebViewClient()
    /** WebView instance */
    val webView: WebView = WebView(context).also {
        it.webViewClient = webViewClient
    }

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


    /** Load html file into WebView */
    override fun loadHtml(html: String, controller: Controller, model: Model?) {
        launch(UI) {
            webView.loadUrl(viewResourcePrefix + html)
            webViewClient.runOnFinishedPage {
                setControllerAndModel(controller, model)
            }
        }
    }

    /** Set Controller for callbacks in ui */
    @SuppressLint("JavascriptInterface")
    private fun setControllerAndModel(controller: Controller, model: Model?) {
        Log.d(this@AndroidWebViewService.javaClass.simpleName, "add controller 1")
        launch(UI) {
            Log.d(this@AndroidWebViewService.javaClass.simpleName, "add controller 2")
            webView.removeJavascriptInterface(WebViewService.javascriptControllerModuleName)
            webView.removeJavascriptInterface(WebViewService.javascriptModelModuleName)
            webView.addJavascriptInterface(controller, WebViewService.javascriptControllerModuleName)
            if (model != null) webView.addJavascriptInterface(model, WebViewService.javascriptModelModuleName)
        }
    }

    /** execute javascript in current document */
    override fun executeJS(script: String) {
        webViewClient.runOnFinishedPage {
            launch(UI) {
                webView.evaluateJavascript(script) {}
            }
        }
    }

    /**
     * Like [WebViewService.viewResourcePrefix] but specific for android
     */
    private val viewResourcePrefix: String = "file:///android_asset" + WebViewService.viewResourcePrefix.removePrefix("/assets")


    private class LoadStatusWebViewClient : WebViewClient() {

        private val onPageFinishedTasks: MutableList<(() -> Unit)> = mutableListOf()
        private var finishedPage: Boolean by Delegates.observable(true) { _, old, new ->
            if (new && !old) onPageFinishedTasks.forEach { task ->
                Log.d(this@LoadStatusWebViewClient.javaClass.simpleName, "run task")
                task()
                onPageFinishedTasks.remove(task)
            }
        }

        fun runOnFinishedPage(task: () -> Unit) {
            if (finishedPage) task()
            else onPageFinishedTasks += task
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            finishedPage = false
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            finishedPage = true
            Log.d(this.javaClass.simpleName, "onPageFinished - ${onPageFinishedTasks.size} tasks to do")
        }
    }

}