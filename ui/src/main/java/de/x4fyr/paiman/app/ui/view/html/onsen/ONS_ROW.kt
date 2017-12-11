package de.x4fyr.paiman.app.ui.view.html.onsen

import kotlinx.html.*

/** ons-row */
class ONS_ROW(consumer: TagConsumer<*>): HTMLTag(tagName = "ons-row",
        consumer = consumer, initialAttributes = emptyMap(), inlineTag = false,
        emptyTag = false), HtmlBlockTag

/** ons-row */
fun FlowContent.onsRow(block: ONS_ROW.() -> Unit) = ONS_ROW(
        consumer).visit(block)
