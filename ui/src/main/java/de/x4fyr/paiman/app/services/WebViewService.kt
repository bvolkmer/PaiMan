package de.x4fyr.paiman.app.services

import de.x4fyr.paiman.app.ui.Controller
import netscape.javascript.JSObject
import org.w3c.dom.Element

/** Service handling rendering and interaction with the native web view */
interface WebViewService {

    /** Load ui from content in [appendable]
     *
     * It shall replace
     */
    fun loadUI(htmlElement: Element)

    /** Set Controller for callbacks in ui */
    fun setCallbackController(controller: Controller)

    /** Invoke javascript on webview */
    fun invoke(command: String) : JSObject?

    companion object {
        /** controller interface name used in javascript/ui */
        const val javascriptModuleName = "controller"
    }
}