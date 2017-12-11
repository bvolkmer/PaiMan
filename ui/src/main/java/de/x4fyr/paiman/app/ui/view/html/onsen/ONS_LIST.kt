package de.x4fyr.paiman.app.ui.view.html.onsen

import kotlinx.html.*

/** ons-list */
class ONS_LIST(consumer: TagConsumer<*>): HTMLTag(tagName = "ons-list",
        consumer = consumer, initialAttributes = emptyMap(), inlineTag = false,
        emptyTag = false), HtmlBlockTag

/** ons-list */
fun FlowContent.onsList(block: ONS_LIST.() -> Unit) = ONS_LIST(
        consumer).visit(block)
