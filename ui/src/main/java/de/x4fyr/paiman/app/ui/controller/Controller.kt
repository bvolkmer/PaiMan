package de.x4fyr.paiman.app.ui.controller

import de.x4fyr.paiman.app.ui.view.View

/** Base interface to all controller */
interface Controller {

    /** Prepare and load corresponding [View] */
    fun loadView()
}