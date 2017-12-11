package de.x4fyr.paiman.app.ui.view.html.onsen

import kotlinx.html.*

/** ons-toolbar **/
class ONS_TOOLBAR(consumer: TagConsumer<*>): HTMLTag(tagName = "ons-toolbar",
        consumer = consumer, initialAttributes = emptyMap(), inlineTag = false,
        emptyTag = false), HtmlBlockTag, FlowContent


/** ons-toolbar **/
fun FlowContent.onsToolbar(block: ONS_TOOLBAR.() -> Unit) = ONS_TOOLBAR(
        consumer).visit(block)
