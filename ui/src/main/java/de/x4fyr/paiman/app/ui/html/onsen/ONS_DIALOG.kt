package de.x4fyr.paiman.app.ui.html.onsen

import kotlinx.html.*

/** ons-dialog */
class ONS_DIALOG(consumer: TagConsumer<*>): HTMLTag(tagName = tagName,
        consumer = consumer, initialAttributes = emptyMap(), inlineTag = false,
        emptyTag = false), HtmlBlockTag {
    companion object {
        /** HTML tag name */
        const val tagName = "ons-dialog"
    }
}

/** ons-dialog */
fun FlowContent.onsDialog(block: ONS_DIALOG.() -> Unit) = ONS_DIALOG(
        consumer).visit(block)

/** ons-dialog */
fun <T, C: TagConsumer<T>> C.onsDialog(block: ONS_DIALOG.() -> Unit) = ONS_DIALOG(
        consumer = this).visit(block)
