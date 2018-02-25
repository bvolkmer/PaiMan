package de.x4fyr.paiman.app.ui.views.entry

import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.View
import de.x4fyr.paiman.app.ui.defaultBody
import de.x4fyr.paiman.app.ui.defaultHead
import de.x4fyr.paiman.app.ui.html.onsen.onsButton
import de.x4fyr.paiman.app.ui.html.onsen.onsProgressCircular
import kotlinx.coroutines.experimental.async
import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.dom.document

/** View shown, when entering the application */
class EntryView: View {

    /** DOM Element of this view */
    override val element by lazy {
        async {
            document {}.create.html {
                defaultHead {}
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
                    onsButton("${WebViewService.javascriptModuleName}.openNext()") { +"Next" }
                })
            }
        }
    }
}
