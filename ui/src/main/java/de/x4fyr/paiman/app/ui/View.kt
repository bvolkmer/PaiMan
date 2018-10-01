package de.x4fyr.paiman.app.ui

import kotlinx.coroutines.experimental.Deferred
import org.w3c.dom.Element

/** Base interface for all views */
interface View {
    /** Element to be shown on this view */
    val element: Deferred<Element>
}