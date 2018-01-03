package de.x4fyr.paiman.app.ui.view

import de.x4fyr.paiman.app.adapter.WebResourceAdapter
import de.x4fyr.paiman.app.ui.controller.MainViewPaintingModel
import de.x4fyr.paiman.app.ui.view.html.onsen.onsCard
import de.x4fyr.paiman.app.ui.view.html.onsen.onsRow
import kotlinx.html.div
import kotlinx.html.html
import kotlinx.html.stream.appendHTML

/** [View] including overview painting listing */
class MainView(private val resourceAdapter: WebResourceAdapter): View {
    /** Append html ui to [appendable] based on given [models] of title and content */
    fun appendTo(models: Set<MainViewPaintingModel>, appendable: Appendable) {
        appendable.appendHTML().html {
            defaultHead(resourceAdapter)
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