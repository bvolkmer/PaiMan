package de.x4fyr.paiman.app.services

import de.x4fyr.paiman.app.ui.controller.Controller

/** Service handling rendering and interaction with the native web view */
interface WebViewService {

    /** Load ui from content in [appendable] */
    fun loadUI(appendable: Appendable)

    /** Set Controller for callbacks in ui */
    fun setCallbackController(controller: Controller)

    companion object {
        /** controller interface name used in javascript/ui */
        const val javascriptModuleName = "controller"
    }
}