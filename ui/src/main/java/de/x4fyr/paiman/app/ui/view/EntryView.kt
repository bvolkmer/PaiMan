package de.x4fyr.paiman.app.ui.view

import de.x4fyr.paiman.app.ui.view.html.onsen.onsButton
import de.x4fyr.paiman.app.ui.view.html.onsen.onsProgressCircular
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.html
import kotlinx.html.stream.appendHTML

/** View shown, when entering the application */
class EntryView: View {

     /** Append ui html to [appendable] */
     fun appendTo(appendable: Appendable) {
        appendable.appendHTML().html {
            defaultHead()
            defaultBody({
                div("center") {
                    +"PaiMan"
                }
            }, {
                h1 {
                    +"Loading"
                }
                onsProgressCircular(indeterminate = true)
                onsButton("controller.openNext()") { +"Next"}
            })
        }
    }
}