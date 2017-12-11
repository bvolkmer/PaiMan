package de.x4fyr.paiman.app.ui.view.html.onsen

import kotlinx.html.*

/** ons-list-header */
class ONS_LIST_HEADER(consumer: TagConsumer<*>): HTMLTag(tagName = "ons-list-header",
        consumer = consumer, initialAttributes = emptyMap(), inlineTag = false,
        emptyTag = false), HtmlBlockTag, FlowContent

/** ons-list-header */
fun ONS_LIST.onsListHeader(block: ONS_LIST_HEADER.() -> Unit) = ONS_LIST_HEADER(
        consumer).visit(block)
