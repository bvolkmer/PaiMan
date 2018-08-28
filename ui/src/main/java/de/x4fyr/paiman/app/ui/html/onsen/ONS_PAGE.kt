package de.x4fyr.paiman.app.ui.html.onsen

import kotlinx.html.*

/** ons-page **/
class ONS_PAGE(consumer: TagConsumer<*>): HTMLTag(tagName = "ons-page",
        consumer = consumer, initialAttributes = emptyMap(), inlineTag = false,
        emptyTag = false), HtmlBlockTag, FlowContent

/** ons-page **/
fun BODY.onsPage(block: ONS_PAGE.() -> Unit) = ONS_PAGE(
        consumer).visit(block)

/** ons-page **/
fun ONS_SPLITTER_CONTENT.onsPage(block: ONS_PAGE.() -> Unit) = ONS_PAGE(
        consumer).visit(block)

/** ons-page **/
fun ONS_SPLITTER_SIDE.onsPage(block: ONS_PAGE.() -> Unit) = ONS_PAGE(
        consumer).visit(block)
