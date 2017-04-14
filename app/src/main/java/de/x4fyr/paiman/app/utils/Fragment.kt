package de.x4fyr.paiman.app.utils

import de.x4fyr.paiman.app.ui.MainView
import tornadofx.*

/**
 * Fragment with implemented escape/end key event handling
 */
abstract class Fragment : tornadofx.Fragment() {

    protected val defaultReferrer = find<MainView>()
    /** UIComponent replaced by this, which should be returned to by default */
    val referrer by param<UIComponent>(defaultReferrer)

    init {
        onEscapeReleased { backToReferrer() }
    }

    protected fun backToReferrer() {
        replaceWith(referrer)
    }
}