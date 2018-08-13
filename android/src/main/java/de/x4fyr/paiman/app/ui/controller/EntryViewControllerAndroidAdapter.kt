package de.x4fyr.paiman.app.ui.controller

import android.webkit.JavascriptInterface
import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.views.entry.EntryController
import de.x4fyr.paiman.app.ui.views.entry.EntryView
import de.x4fyr.paiman.app.ui.views.overview.OverviewController

/** Adapter for [EntryController] to make functions available as javascript interface  */
class EntryViewControllerAndroidAdapter(webViewService: WebViewService, mainViewController: OverviewController, entryView: EntryView): EntryController(webViewService, mainViewController, entryView) {

    @JavascriptInterface
    override fun openNext() {
        super.openNext()
    }
}