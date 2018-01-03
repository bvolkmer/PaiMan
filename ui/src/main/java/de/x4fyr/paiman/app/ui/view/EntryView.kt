package de.x4fyr.paiman.app.ui.view

import de.x4fyr.paiman.app.adapter.WebResourceAdapter
import de.x4fyr.paiman.app.ui.view.html.onsen.onsButton
import de.x4fyr.paiman.app.ui.view.html.onsen.onsProgressCircular
import kotlinx.html.*
import kotlinx.html.stream.appendHTML

/** View shown, when entering the application */
class EntryView(private val resourceAdapter: WebResourceAdapter): View {

    /** Append ui html to [appendable] */
    fun appendTo(appendable: Appendable) {
        appendable.appendHTML().html {
            defaultHead(resourceAdapter) {
                script {
                    unsafe { +"""window.onload = function () { document.getElementById("test").innerText = "test 2"}""" }
                }
            }
            defaultBody({
                div("center") {
                    +"PaiMan"
                }
            }, {
                h1 {
                    +"Loading"
                }
                p {
                    id = "test"
                    +"test 1"
                }
                onsProgressCircular(indeterminate = true)
                onsButton("controller.openNext()") { +"Next" }
            })
        }
    }
}