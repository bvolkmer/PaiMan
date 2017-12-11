package de.x4fyr.paiman.app.ui.view.html.onsen

import kotlinx.html.FlowContent
import kotlinx.html.HTMLTag
import kotlinx.html.TagConsumer
import kotlinx.html.visit

/** ons-progress-circular */
class ONS_PROGRESS_CIRCULAR(initialAttributes: Map<String, String> = emptyMap(), consumer: TagConsumer<*>): HTMLTag(
        tagName = "ons-progress-circular", consumer = consumer, initialAttributes = initialAttributes, inlineTag = false,
        emptyTag = true)

/** ons-progress-circular with default settings **/
fun FlowContent.onsProgressCircular(value: Int? = null, secondaryValue: Int? = null, indeterminate: Boolean = false) {
    val attributes: MutableMap<String, String> = mutableMapOf()
    if (indeterminate) attributes += "indeterminate".to("")
    else {
        if (value != null) attributes += "value".to(value.toString())
        if (secondaryValue != null) attributes += "secondary-value".to(secondaryValue.toString())
    }
    ONS_PROGRESS_CIRCULAR(attributes, consumer).visit {
    }
}
