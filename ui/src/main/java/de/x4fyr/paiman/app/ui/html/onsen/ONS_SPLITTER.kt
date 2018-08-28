package de.x4fyr.paiman.app.ui.html.onsen

import kotlinx.html.*

/** ons-splitter **/
class ONS_SPLITTER(consumer: TagConsumer<*>): HTMLTag(tagName = "ons-splitter",
        consumer = consumer, initialAttributes = emptyMap(), inlineTag = false,
        emptyTag = false), HtmlBlockTag

/** ons-splitter **/
fun BODY.onsSplitter(block: ONS_SPLITTER.() -> Unit) = ONS_SPLITTER(
        consumer).visit(block)

