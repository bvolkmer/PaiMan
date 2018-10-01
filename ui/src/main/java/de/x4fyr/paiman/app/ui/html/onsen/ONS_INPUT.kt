package de.x4fyr.paiman.app.ui.html.onsen

import kotlinx.html.*

/** ons-card */
class ONS_INPUT(attributes: Map<String, String> = emptyMap(), consumer: TagConsumer<*>) : HTMLTag(tagName = "ons-input",
        consumer = consumer, initialAttributes = attributes, inlineTag = true,
        emptyTag = false), CommonAttributeGroupFacade {

    var value: String
        get() = attributes.getOrDefault("value", "")
        set(value) = attributes.set("value", value)
}

/** ons-card */
fun FlowContent.onsInput(type: InputType,
                         float: Boolean = false,
                         placeholder: String? = null,
                         inputId: String? = null,
                         value: String = "",
                         block: ONS_INPUT.() -> Unit = {}) =
        ONS_INPUT(mutableMapOf<String, String>().apply {
            this += "type".to(type.realValue)
            if (float) this += "float".to("")
            if (placeholder != null) "placeholder".to(placeholder)
            if (inputId != null) "input-id".to(inputId)
        }, consumer).visit({
            +value
            block()
        })
