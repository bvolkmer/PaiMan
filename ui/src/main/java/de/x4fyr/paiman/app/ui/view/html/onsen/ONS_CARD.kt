package de.x4fyr.paiman.app.ui.view.html.onsen

import kotlinx.html.*

/** ons-card */
class ONS_CARD(consumer: TagConsumer<*>): HTMLTag(tagName = "ons-card",
        consumer = consumer, initialAttributes = emptyMap(), inlineTag = true,
        emptyTag = false), FlowContent

/** ons-card */
fun FlowContent.onsCard(title: DIV.() -> Unit = {}, content: DIV.() -> Unit = {}) = ONS_CARD(consumer).visit({
    div("title", title)
    div("content", content)
})
