package de.x4fyr.paiman.app.ui.html.onsen

import kotlinx.html.*

/** ons-splitter-content **/
class ONS_SPLITTER_CONTENT(consumer: TagConsumer<*>): HTMLTag(tagName = "ons-splitter-content",
        consumer = consumer, initialAttributes = emptyMap(), inlineTag = false,
        emptyTag = false), HtmlBlockTag, FlowContent

/** ons-splitter-content **/
fun ONS_SPLITTER.onsSplitterContent(block: ONS_SPLITTER_CONTENT.() -> Unit) = ONS_SPLITTER_CONTENT(
        consumer).visit(block)

