package de.x4fyr.paiman.app.services

import de.x4fyr.paiman.app.ui.Controller
import de.x4fyr.paiman.app.ui.Model
import org.w3c.dom.Element

/** Service handling rendering and interaction with the native web view */
interface WebViewService {

    /** Load html element into WebView*/
    fun loadUI(htmlElement: Element)

    /** Load html file into WebView */
    fun loadHtml(html: String, controller: Controller, model: Model? = null)

    /** execute javascript in current document */
    fun executeJS(script: String)

    /** Show ui error escapedMessage. Special characters need to be escaped */
    fun showError(escapedMessage: String) {
        executeJS("showError('$escapedMessage')")
    }

    companion object {
        /** controller interface title used in javascript/ui */
        const val javascriptControllerModuleName = "controller"
        /** model interface title used in javascript/ui */
        const val javascriptModelModuleName = "model"
        /** Prefix of resource path of ui files */
        const val viewResourcePrefix: String = "/assets/de/x4fyr/paiman/ui/"
    }

}