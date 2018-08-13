package de.x4fyr.paiman.app

import android.app.Activity
import android.os.Bundle
import android.webkit.WebView
import de.x4fyr.paiman.app.service.AndroidWebViewService
import de.x4fyr.paiman.app.ui.views.entry.EntryController
import de.x4fyr.paiman.dagger.DaggerMainComponent
import de.x4fyr.paiman.dagger.MainComponent
import de.x4fyr.paiman.dagger.android.PlatformModule
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

/** Activity that only wraps a [WebView] and loads ui in it. */
class WebViewWrapperActivity: Activity() {

    private lateinit var webViewService: AndroidWebViewService
    private lateinit var entryViewController: EntryController


    override fun onCreate(savedInstanceState: Bundle?) {
        val component: MainComponent = DaggerMainComponent.builder()
                .platformModule(PlatformModule(this))
                .build()
        webViewService = component.webViewServiceImpl()
        entryViewController = component.entryUIController()
        super.onCreate(savedInstanceState)
        setContentView(webViewService.webView)
        launch(CommonPool) {
            entryViewController.loadView()
        }
    }
}
