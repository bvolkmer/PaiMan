package de.x4fyr.paiman.app.ui.controller

import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.view.MainView

/** Controller for [MainView] */
class MainViewController constructor(private val webViewService: WebViewService,
                                     private val view: MainView): Controller {

    private val model: Map<String, String> by lazy {
        val tmp = mutableMapOf<String, String>()
        for (i in 1..10) {
            tmp += i.toString().to("Hello World!")
        }
        tmp.toMap()
    }

    /** See [Controller.loadView] */
    override fun loadView() {
        val sb = StringBuilder()
        view.appendTo(model, sb)
        webViewService.loadUI(sb)
    }
}