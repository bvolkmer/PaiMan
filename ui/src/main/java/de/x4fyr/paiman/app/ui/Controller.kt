package de.x4fyr.paiman.app.ui

/** Base interface to all controller */
interface Controller {

    /** Prepare and load corresponding [View] */
    suspend fun loadView()
}