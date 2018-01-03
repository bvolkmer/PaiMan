package de.x4fyr.paiman.app.ui.controller

import android.webkit.JavascriptInterface
import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.view.EntryView

/** Adapter for [EntryViewController] to make functions available as javascript interface  */
class EntryViewControllerAndroidAdapter(webViewService: WebViewService, mainViewController: MainViewController, entryView: EntryView): EntryViewController(webViewService, mainViewController, entryView) {

    @JavascriptInterface
    override fun openNext() {
        super.openNext()
    }
}