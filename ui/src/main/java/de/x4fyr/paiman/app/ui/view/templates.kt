package de.x4fyr.paiman.app.ui.view

import de.x4fyr.paiman.app.ui.view.html.onsen.ONS_PAGE
import de.x4fyr.paiman.app.ui.view.html.onsen.ONS_TOOLBAR
import de.x4fyr.paiman.app.ui.view.html.onsen.onsPage
import de.x4fyr.paiman.app.ui.view.html.onsen.onsToolbar
import kotlinx.html.*

private object Resources {
    /** [Class.getResource] for use in lambdas */
    fun getResource(name: String): String = this::class.java.getResource(name).toExternalForm()
}


/** Default head for use in Views */
fun HTML.defaultHead(block: HEAD.() -> Unit = {}) {
    head {
        onsenHead()
        block()
    }
}

/** Head block including css, js and config for onsenui */
fun HEAD.onsenHead() {
    styleLink(Resources.getResource("/css/onsenui.css"))
    styleLink(Resources.getResource("/css/onsen-css-components.min.css"))
    script(type = ScriptType.textJavaScript, src = Resources.getResource("/js/onsenui.min.js")) {}
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