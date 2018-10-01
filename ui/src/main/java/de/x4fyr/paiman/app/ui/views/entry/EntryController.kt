package de.x4fyr.paiman.app.ui.views.entry

import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.Controller
import de.x4fyr.paiman.app.ui.views.overview.OverviewController
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

/** Entry Controller */
open class EntryController(private val webViewService: WebViewService,
                           private val mainViewController: OverviewController): Controller {

    override suspend fun loadView() {
        webViewService.loadHtml(html, this)
    }

    /** Callback: Open next view
     *
     * unused because only used in ui via js
     * open to allow adding annotations that be might be use on some platforms for javascript injection
     */
    @Suppress("unused")
    open fun openNext() {
        println("Callback: openNext()")
        launch(CommonPool) {
            mainViewController.loadView()
        }
    }

    companion object {
        private const val html = "index.html"
    }
}