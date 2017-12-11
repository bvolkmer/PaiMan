package de.x4fyr.paiman.app.ui.view.html.onsen

import kotlinx.html.HTMLTag
import kotlinx.html.HtmlBlockTag
import kotlinx.html.TagConsumer
import kotlinx.html.visit

/** ons-col */
class ONS_COL(width: String? = null, consumer: TagConsumer<*>): HTMLTag(tagName = "ons-col",
        consumer = consumer,
        initialAttributes = {
            val attributes = mutableMapOf<String, String>()
            if (width != null) {
                attributes += "width".to(width)
            }
            attributes
        }(),
        inlineTag = false,
        emptyTag = false), HtmlBlockTag

/** ons-col */
fun ONS_ROW.onsCol(width: String? = null, block: ONS_COL.() -> Unit) = ONS_COL(
        width, consumer).visit(block)
