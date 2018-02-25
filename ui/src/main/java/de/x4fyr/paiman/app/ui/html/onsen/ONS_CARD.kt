package de.x4fyr.paiman.app.ui.html.onsen

import kotlinx.html.*

/** ons-card */
class ONS_CARD(consumer: TagConsumer<*>): HTMLTag(tagName = "ons-card",
        consumer = consumer, initialAttributes = emptyMap(), inlineTag = true,
        emptyTag = false), FlowContent

/** ons-card */
fun FlowContent.onsCard(title: DIV.() -> Unit = {}, content: DIV.() -> Unit = {}) = ONS_CARD(
        consumer).visit({
    div("title", title)
    div("content", content)
})

/** ons-card */
fun <T, C: TagConsumer<T>> C.onsCard(title: (DIV.() -> Unit)? = null, content: (DIV.() -> Unit)? = null) = ONS_CARD(
        this).visitAndFinalize(this, {
    if (title != null) div("title", title)
    if (content != null) div("content", content)
})
