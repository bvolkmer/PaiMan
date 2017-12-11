package de.x4fyr.paiman.app.ui.view

import de.x4fyr.paiman.app.ui.view.html.onsen.onsCard
import de.x4fyr.paiman.app.ui.view.html.onsen.onsRow
import kotlinx.html.div
import kotlinx.html.html
import kotlinx.html.stream.appendHTML

/** [View] including overview painting listing */
class MainView: View {
    /** Append html ui to [appendable] based on given [model] of title and content */
    fun appendTo(model: Map<String, String>, appendable: Appendable) {
        appendable.appendHTML().html {
            defaultHead {}
            defaultBody({
                div("center") {
                    +"Paiman"
                }
            }, {
                onsRow {
                    for ((title, content) in model) {
                        onsCard(title = { +title }, content = { +content })
                    }
                }
            })
        }
    }

}