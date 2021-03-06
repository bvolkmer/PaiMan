package de.x4fyr.paiman.app.ui.html.onsen

import kotlinx.html.FlowContent
import kotlinx.html.HTMLTag
import kotlinx.html.TagConsumer
import kotlinx.html.visit

/** ons-progress-bar **/
class ONS_PROGRESS_BAR(initialAttributes: Map<String, String> = emptyMap(), consumer: TagConsumer<*>): HTMLTag(
        tagName = "ons-progress-bar", consumer = consumer, initialAttributes = initialAttributes, inlineTag = false,
        emptyTag = false)

/** ons-progress-bar with default settings **/
fun FlowContent.onsProgressBar(value: Int? = null, secondaryValue: Int? = null, indeterminate: Boolean = false) {
    val attributes: MutableMap<String, String> = mutableMapOf()
    if (indeterminate) attributes += "indeterminate".to("")
    else {
        if (value != null) attributes += "value".to(value.toString())
        if (secondaryValue != null) attributes += "secondary-value".to(secondaryValue.toString())
    }
    ONS_PROGRESS_BAR(attributes, consumer).visit {}
}
