package de.x4fyr.paiman.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import de.x4fyr.paiman.app.dagger.AndroidModule
import de.x4fyr.paiman.app.dagger.DaggerMainComponent
import de.x4fyr.paiman.app.dagger.MainComponent
import de.x4fyr.paiman.app.service.AndroidWebViewService
import de.x4fyr.paiman.app.ui.controller.EntryViewController
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

/** Activity that only wraps a [WebView] and loads ui in it. */
class WebViewWrapperActivity: AppCompatActivity() {

    private lateinit var webViewService: AndroidWebViewService
    private lateinit var entryViewController: EntryViewController


    override fun onCreate(savedInstanceState: Bundle?) {
        val component: MainComponent = DaggerMainComponent.builder()
                .androidModule(AndroidModule(this))
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
