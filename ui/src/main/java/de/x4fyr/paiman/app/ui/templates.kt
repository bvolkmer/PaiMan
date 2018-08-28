package de.x4fyr.paiman.app.ui

import de.x4fyr.paiman.app.ui.html.onsen.*
import kotlinx.html.*
import java.io.InputStream
import java.util.*

/** Default head for use in Views */
fun HTML.defaultHead(block: HEAD.() -> Unit = {}) {
    head {
        onsenHead()
        block()
    }
}

/** Head block including css, js and config for onsenui */
fun HEAD.onsenHead() {
    link(href = "./css/onsenui-core.min.css", rel = "stylesheet")
    link(href = "./css/onsen-css-components.min.css", rel = "stylesheet")
    link(href = "./css/fontawesome-all.css", rel = "stylesheet")
    link(href = "./css/main.css", rel = "stylesheet")
    script(type = ScriptType.textJavaScript, src = "./js/onsenui.min.js") {}
    unsafe { raw("<script>ons.platform.select('android')</script>") }
}

/** Default body with toolbar */
fun HTML.defaultBody(toolbarContent: ONS_TOOLBAR.() -> Unit, block: ONS_PAGE.() -> Unit) {
    defaultBodyWithoutToolbar {
        onsToolbar {
            toolbarContent()
        }
        block()
    }
}

fun HTML.splitterBody(side: String, sideId: String? = null, toolbarContent: ONS_TOOLBAR.() -> Unit, sideContent: ONS_PAGE.() -> Unit, content: ONS_PAGE.() -> Unit) {
    body {
        onsSplitter {
            onsSplitterSide(side = side, attributes = mapOf("collapse" to "", "swipeable" to "")) {
                if (sideId != null) id = sideId
                onsPage(sideContent)
            }
            onsSplitterContent {
                onsPage {
                    onsToolbar(toolbarContent)
                    content()
                }
            }
        }
    }
}

/** Default body without toolbar */
fun HTML.defaultBodyWithoutToolbar(block: ONS_PAGE.() -> Unit) {
    body {
        onsPage {
            block()
        }
    }

}

internal fun jpegDataString(base64Image: String) = "data:image/jpeg;base64,$base64Image"

internal fun jpegDataString(stream: InputStream) = jpegDataString(Base64.getEncoder().encodeToString(stream.readAllBytes()))
