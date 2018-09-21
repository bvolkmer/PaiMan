package de.x4fyr.paiman.app.services

import de.x4fyr.paiman.app.ui.Controller
import de.x4fyr.paiman.app.ui.Model
import org.w3c.dom.Element

/** Service handling rendering and interaction with the native web view */
interface WebViewService {

    /** Load ui from content in [appendable]
     *
     * It shall replace
     */
    fun loadUI(htmlElement: Element)

    /** Set Controller for callbacks in ui */
    fun setControllerAndModel(controller: Controller, model: Model? = null)

    companion object {
        /** controller interface name used in javascript/ui */
        const val javascriptControllerModuleName = "controller"
        const val javascriptModelModuleName = "model"
    }

    /** Load html file */
    fun loadHtml(html: String, controller: Controller, model: Model? = null)

}