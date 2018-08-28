package de.x4fyr.paiman.app.ui.html.onsen

import kotlinx.html.*

/** ons-toolbar-button **/
class ONS_TOOLBAR_BUTTON(attributes: Map<String, String> = emptyMap(), consumer: TagConsumer<*>) : HTMLTag(
        tagName = "ons-toolbar-button", consumer = consumer, initialAttributes = attributes, inlineTag = true,
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

/** ons-toolbar-button **/
fun FlowContent.onsToolbarButton(block: ONS_TOOLBAR_BUTTON.() -> Unit) = ONS_TOOLBAR_BUTTON(
        consumer = consumer).visit(block)

/** ons-toolbar-button with [ONS_TOOLBAR_BUTTON.onClick] quick setting **/
fun FlowContent.onsToolbarButton(onClick: String, block: ONS_TOOLBAR_BUTTON.() -> Unit = {}) = ONS_TOOLBAR_BUTTON(
        consumer = consumer).visit {
    this.onClick = onClick
    block()
}

/** ons-button with modifier and [ONS_BUTTON.onClick] quick setting **/
fun FlowContent.onsToolbarButton(onClick: String = "", modifier: ONS_TOOLBAR_BUTTON.Modifier, block: ONS_TOOLBAR_BUTTON.() -> Unit = {}) =
        ONS_TOOLBAR_BUTTON(
                attributes = mutableMapOf<String, String>().apply {
                    this += "modifier" to modifier.realValue
                }, consumer = consumer).visit {
            if (!onClick.isEmpty()) {
                this.onClick = onClick
            }
            block()
        }


/** ons-toolbar-button with modifier and [ONS_TOOLBAR_BUTTON.onClick] quick setting **/
fun <T, C : TagConsumer<T>> C.onsToolbarButton(onClick: String = "", modifier: ONS_TOOLBAR_BUTTON.Modifier, block: ONS_TOOLBAR_BUTTON.() -> Unit

= {}) =
        ONS_TOOLBAR_BUTTON(
                attributes = mutableMapOf<String, String>().apply {
                    this += "modifier".to(modifier.realValue)
                }, consumer = this).visitAndFinalize(this) {
            if (!onClick.isEmpty()) {
                this.onClick = onClick
            }
            block()
        }