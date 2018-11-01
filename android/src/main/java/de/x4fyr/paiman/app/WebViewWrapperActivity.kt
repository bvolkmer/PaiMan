package de.x4fyr.paiman.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import de.x4fyr.paiman.app.service.AndroidWebViewService
import de.x4fyr.paiman.app.ui.views.entry.EntryController
import de.x4fyr.paiman.dagger.DaggerMainComponent
import de.x4fyr.paiman.dagger.MainComponent
import de.x4fyr.paiman.dagger.android.PlatformModule
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/** Activity that only wraps a [WebView] and loads ui in it. */
class WebViewWrapperActivity : Activity() {

    private lateinit var webViewService: AndroidWebViewService
    private lateinit var entryViewController: EntryController

    private var resultHandlerMap: MutableMap<Any, ((requestCode: Int, resultCode: Int, data: Intent?) -> Unit)> = HashMap()


    override fun onCreate(savedInstanceState: Bundle?) {
        val component: MainComponent = DaggerMainComponent.builder()
                .platformModule(PlatformModule(this))
                .build()
        webViewService = component.webViewServiceImpl()
        entryViewController = component.entryUIController()
        super.onCreate(savedInstanceState)
        setContentView(webViewService.webView)
        GlobalScope.launch {
            entryViewController.loadView()
        }
    }

    /** Add a handler to be invoked on [Activity.onActivityResult] */
    fun addActivityResultHandler(identifier: Any, handler: (requestCode: Int, resultCode: Int, data: Intent?) -> Unit) {
        resultHandlerMap.put(identifier, handler)
    }

    /** Remove a handler not to be invoked on [Activity.onActivityResult] */
    fun removeActivityResultHandler(identifier: Any) {
        resultHandlerMap.remove(identifier)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        resultHandlerMap.forEach { _, handler ->
            handler.invoke(requestCode, resultCode, data)
        }
    }

}
