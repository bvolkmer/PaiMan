package de.x4fyr.paiman.app.ui.html.onsen

import kotlinx.html.*

/** ons-row */
class ONS_ROW(consumer: TagConsumer<*>): HTMLTag(tagName = tagName,
        consumer = consumer, initialAttributes = emptyMap(), inlineTag = false,
        emptyTag = false), HtmlBlockTag {
    companion object {
        /** HTML tag name */
        val tagName = "ons-row"
    }
}

/** ons-row */
fun FlowContent.onsRow(block: ONS_ROW.() -> Unit) = ONS_ROW(
        consumer).visit(block)

fun <T, C: TagConsumer<T>> C.onsRow(block: ONS_ROW.() -> Unit) = ONS_ROW(
        consumer = this).visit(block)
