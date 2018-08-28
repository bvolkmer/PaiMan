package de.x4fyr.paiman.app.ui.html.onsen

import kotlinx.html.*

/** ons-splitter-content **/
class ONS_SPLITTER_SIDE(consumer: TagConsumer<*>) : HTMLTag(tagName = "ons-splitter-side",
        consumer = consumer, initialAttributes = emptyMap(), inlineTag = false,
        emptyTag = false), HtmlBlockTag, FlowContent

/** ons-splitter-side **/
fun ONS_SPLITTER.onsSplitterSide(side: String, attributes: Map<String, String> = mapOf(), block: ONS_SPLITTER_SIDE.() -> Unit) = ONS_SPLITTER_SIDE(
        consumer).visit {
    this.attributes += "side" to side
    this.attributes += attributes
    block()
}

