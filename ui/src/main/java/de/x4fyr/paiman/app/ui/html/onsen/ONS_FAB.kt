package de.x4fyr.paiman.app.ui.html.onsen

import kotlinx.html.*

/** ons-fab **/
class ONS_FAB(attributes: Map<String, String> = emptyMap(), consumer: TagConsumer<*>): HTMLTag
(tagName = "ons-fab",
        consumer = consumer, initialAttributes = attributes, inlineTag = false,
        emptyTag = false), HtmlBlockTag, FlowContent {

    /** Present Modifier */
    enum class Modifier(
            /** Value used in html */
            val realValue: String) {
        /** Makes the ons-fab smaller. */
        MINI("mini")
    }

    /** Position of the fab in [ONS_PAGE] */
    enum class Position(
            /** Value used in html */
            val realValue: String) {
        /** Top left position */
        TOP_LEFT("top left"),
        /** Top right position */
        TOP_RIGHT("top right"),
        /** Bottom left position */
        BOTTOM_LEFT("bottom left"),
        /** Bottom right position */
        BOTTOM_RIGHT("bottom right")
    }

}

/** ons-fab **/
fun FlowContent.onsFab(modifier: ONS_FAB.Modifier? = null,
                       ripple: Boolean = false,
                       position: ONS_FAB.Position,
                       disabled: Boolean = false,
                       onClick: String = "",
                       block: ONS_FAB.() -> Unit = {}) = ONS_FAB(
        attributes = mutableMapOf<String, String>().apply {
            this += "position".to(position.realValue)
            if (onClick != "") this += "onClick".to(onClick)
            if (modifier != null) this += "modifier".to(modifier.realValue)
            if (ripple) this += "ripple".to("")
            if (disabled) this += "disabled".to("")
        },
        consumer = consumer).visit(block)