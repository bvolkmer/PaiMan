package de.x4fyr.paiman.app.ui.view.html.onsen

import kotlinx.html.*

/** ons-button **/
class ONS_BUTTON(consumer: TagConsumer<*>): HTMLTag(tagName = "ons-button",
        consumer = consumer, initialAttributes = emptyMap(), inlineTag = true,
        emptyTag = false), CommonAttributeGroupFacadeFlowInteractivePhrasingContent

/** ons-button **/
fun FlowContent.onsButton(block: ONS_BUTTON.() -> Unit) = ONS_BUTTON(consumer).visit(block)

/** ons-button with [ONS_BUTTON.onClick] quick setting **/
fun FlowContent.onsButton(onClick: String, block: ONS_BUTTON.() -> Unit = {}) = ONS_BUTTON(consumer).visit({
    this.onClick = onClick
    block()
})
