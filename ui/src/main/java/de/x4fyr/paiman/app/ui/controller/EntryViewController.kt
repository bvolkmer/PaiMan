package de.x4fyr.paiman.app.ui.controller

import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.view.EntryView
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

/** Controller for [EntryView] */
class EntryViewController constructor(private val webViewService: WebViewService,
                                      private val mainUIController: MainViewController,
                                      private val view: EntryView): Controller {

    override suspend fun loadView() {
        val sb = StringBuilder()
        view.appendTo(sb)
        webViewService.loadUI(sb)
        webViewService.setCallbackController(this)
    }

    /** Callback: Open next view
     *
     * unused because only used in ui via js
     */
    @Suppress("unused")
    fun openNext() {
        launch(CommonPool) {
            mainUIController.loadView()
        }
    }
}