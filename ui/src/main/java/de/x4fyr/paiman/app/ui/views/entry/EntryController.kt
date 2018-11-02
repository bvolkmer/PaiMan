package de.x4fyr.paiman.app.ui.views.entry

import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.Controller
import de.x4fyr.paiman.app.ui.views.overview.OverviewController
import kotlinx.coroutines.launch
import kotlinx.coroutines.GlobalScope

/** Entry Controller */
open class EntryController(private val webViewService: WebViewService,
                           private val mainViewController: OverviewController): Controller {

    override suspend fun loadView() {
        webViewService.loadHtml(html, this)
        GlobalScope.launch {
            mainViewController.loadView()
        }
    }

    companion object {
        private const val html = "index.html"
    }
}