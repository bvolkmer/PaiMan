package de.x4fyr.paiman.app.ui.html.onsen

import kotlinx.html.*

/** ons-list-item */
class ONS_LIST_ITEM(consumer: TagConsumer<*>): HTMLTag(tagName = "ons-list-item",
        consumer = consumer, initialAttributes = emptyMap(), inlineTag = false,
        emptyTag = false), HtmlBlockTag, FlowContent

/** ons-list-item */
fun ONS_LIST.onsListItem(block: ONS_LIST_ITEM.() -> Unit) = ONS_LIST_ITEM(
        consumer).visit(block)
