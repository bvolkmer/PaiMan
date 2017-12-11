package de.x4fyr.paiman.app.ui.view

import de.x4fyr.paiman.app.ui.controller.MainViewPaintingModel
import de.x4fyr.paiman.app.ui.view.html.onsen.onsCard
import de.x4fyr.paiman.app.ui.view.html.onsen.onsRow
import kotlinx.html.div
import kotlinx.html.html
import kotlinx.html.stream.appendHTML

/** [View] including overview painting listing */
class MainView: View {
    /** Append html ui to [appendable] based on given [models] of title and content */
    fun appendTo(models: Set<MainViewPaintingModel>, appendable: Appendable) {
        appendable.appendHTML().html {
            defaultHead {}
            defaultBody({
                div("center") {
                    +"Paiman"
                }
            }, {
                onsRow {
                    for ((title) in models) {
                        onsCard(title = { +title })
                    }
                }
            })
        }
    }

}