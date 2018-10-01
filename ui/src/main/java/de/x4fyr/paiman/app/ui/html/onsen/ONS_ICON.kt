package de.x4fyr.paiman.app.ui.html.onsen

import kotlinx.html.FlowContent
import kotlinx.html.HTMLTag
import kotlinx.html.TagConsumer
import kotlinx.html.visit

/** ons-page **/
class ONS_ICON(attributes: Map<String, String> = emptyMap(), consumer: TagConsumer<*>): HTMLTag
(tagName = "ons-icon",
        consumer = consumer, initialAttributes = attributes, inlineTag = true,
        emptyTag = true), FlowContent {

    /** Relative size of icon */
    enum class RelativeSize(
            /** Value used in html */
            val realValue: String) {
        /** Large */
        LG("lg"),
        /** Double size (2x) */
        DOUBLE("2x"),
        /** Triple size (3x)*/
        TRIPLE("3x"),
        /** Quadruple size (4x)*/
        QUADRUPLE("4x"),
        /** Quituple size (5x)*/
        QUITUPLE("5x")
    }

    /** Rotation of the icon */
    enum class Rotation(
            /** Value used in html */
            val realValue: String) {
        /** 90deg */
        QUARTER("90"),
        /** 180deg */
        HALF("180"),
        /** 270deg */
        THREE_QUARTER("270")
    }
}

/** ons-icon **/
fun FlowContent.onsIcon(icon: String,
                        size: Int? = null,
                        rotate: ONS_ICON.Rotation? = null,
                        fixedWidth: Boolean = false,
                        spin: Boolean = false,
                        block: ONS_ICON.() -> Unit = {}) =
        ONS_ICON(
                attributes = mutableMapOf<String, String>().apply {
                    this += "icon".to(icon)
                    if (size != null) "size".to("${size}px")
                    if (rotate != null) "rotate".to(rotate.realValue)
                    if (fixedWidth) "fixedWidth".to("")
                    if (spin) spin.to("")
                },
                consumer = consumer).visit(block)

/** ons-icon **/
fun FlowContent.onsIcon(icon: String,
                        size: ONS_ICON.RelativeSize,
                        rotate: ONS_ICON.Rotation? = null,
                        fixedWidth: Boolean = false,
                        spin: Boolean = false,
                        block: ONS_ICON.() -> Unit = {}) =
        ONS_ICON(
                attributes = mutableMapOf<String, String>().apply {
                    this += "icon".to(icon)
                    "size".to(size.realValue)
                    if (rotate != null) "rotate".to(rotate.realValue)
                    if (fixedWidth) "fixedWidth".to("")
                    if (spin) spin.to("")
                },
                consumer = consumer).visit(block)
