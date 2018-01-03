package de.x4fyr.paiman.app.ui.view

import de.x4fyr.paiman.app.adapter.WebResourceAdapter
import de.x4fyr.paiman.app.ui.view.html.onsen.ONS_PAGE
import de.x4fyr.paiman.app.ui.view.html.onsen.ONS_TOOLBAR
import de.x4fyr.paiman.app.ui.view.html.onsen.onsPage
import de.x4fyr.paiman.app.ui.view.html.onsen.onsToolbar
import kotlinx.html.*

/** Default head for use in Views */
fun HTML.defaultHead(resourceAdapter: WebResourceAdapter, block: HEAD.() -> Unit = {}) {
    head {
        onsenHead(resourceAdapter)
        block()
    }
}

/** Head block including css, js and config for onsenui */
fun HEAD.onsenHead(resourceAdapter: WebResourceAdapter) {
    style { unsafe { +resourceAdapter.getResourceText("/css/onsenui.css") } }
    style { unsafe { +resourceAdapter.getResourceText("/css/onsen-css-components.min.css") } }
    script(type = ScriptType.textJavaScript) { unsafe { +resourceAdapter.getResourceText("/js/onsenui.min.js") } }
    script { unsafe { +"ons.platform.select('android')" } }
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

/** Default body without toolbar */
fun HTML.defaultBodyWithoutToolbar(block: ONS_PAGE.() -> Unit) {
    body {
        onsPage {
            block()
        }
    }

}