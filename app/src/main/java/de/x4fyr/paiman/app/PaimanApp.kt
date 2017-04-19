package de.x4fyr.paiman.app

import de.x4fyr.paiman.app.css.Global
import de.x4fyr.paiman.app.ui.InitialLoadingView
import fontAwesomeFx.initialiseFontAwesomeFx
import tornadofx.*

/**
 * @author x4fyr
 * *         Created on 3/16/17.
 */
class PaimanApp : App(InitialLoadingView::class, Global::class) {

    private val platformService = PlatformService.loadProvider(PlatformService::class.java)

    init {
        platformService.preUI()
        initialiseFontAwesomeFx(this)
    }

}
