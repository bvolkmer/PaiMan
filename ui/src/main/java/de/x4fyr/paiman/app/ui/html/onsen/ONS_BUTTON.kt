package de.x4fyr.paiman.app.ui.html.onsen

import kotlinx.html.*

/** ons-button **/
class ONS_BUTTON(attributes: Map<String, String> = emptyMap(), consumer: TagConsumer<*>): HTMLTag(
        tagName = "ons-button", consumer = consumer, initialAttributes = attributes, inlineTag = true,
        emptyTag = false), CommonAttributeGroupFacadeFlowInteractivePhrasingContent {

    /** Present modifier */
    enum class Modifier(
            /** Value used in html */
            val realValue: String) {
        /** Button with outline and transparent background */
        OUTLINE("outline"),
        /** Button that doesn’t stand out. */
        LIGHT("light"),
        /** Button with no outline and or background.. */
        QUIET("quiet"),
        /** Button that really stands out. */
        CTA("cta"),
        /** Large button that covers the width of the screen. */
        LARGE("large"),
        /** Large quiet button. */
        LARGE_QUIET("large--quit"),
        /** Large call to action button. */
        LARG_CTA("large--cta"),
        /** Material Design button */
        MATERIAL("material"),
        /** Material Design flat button */
        MATERIAL_FLAT("meterial--flat")
    }

    /**  If this attribute is defined, the button will have a ripple effect.*/
    var ripple: Boolean = false
        set(value) {
            if (value) {
                attributes.put("ripple", "true")
            } else if (attributes.containsKey("ripple")) attributes.remove("ripple")
        }
}

/** ons-button **/
fun FlowContent.onsButton(block: ONS_BUTTON.() -> Unit) = ONS_BUTTON(
        consumer = consumer).visit(block)

/** ons-button with [ONS_BUTTON.onClick] quick setting **/
fun FlowContent.onsButton(onClick: String, block: ONS_BUTTON.() -> Unit = {}) = ONS_BUTTON(
        consumer = consumer).visit({
    this.onClick = onClick
    block()
})

/** ons-button with modifier and [ONS_BUTTON.onClick] quick setting **/
fun FlowContent.onsButton(onClick: String = "", modifier: ONS_BUTTON.Modifier, block: ONS_BUTTON.() -> Unit = {}) =
    ONS_BUTTON(
            attributes = mutableMapOf<String, String>().apply {
                this += "modifier".to(modifier.realValue)
            }, consumer = consumer).visit {
        if (!onClick.isEmpty()) {
            this.onClick = onClick
        }
    block()
}


/** ons-button with modifier and [ONS_BUTTON.onClick] quick setting **/
fun <T, C: TagConsumer<T>> C.onsButton(onClick: String = "", modifier: ONS_BUTTON.Modifier, block: ONS_BUTTON.() -> Unit
= {}) =
    ONS_BUTTON(
            attributes = mutableMapOf<String, String>().apply {
                this += "modifier".to(modifier.realValue)
            }, consumer = this).visitAndFinalize(this) {
        if (!onClick.isEmpty()) {
            this.onClick = onClick
        }
    block()
}