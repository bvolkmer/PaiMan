package de.x4fyr.paiman.app.ui.controller

import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.view.MainView
import de.x4fyr.paiman.lib.services.PaintingService
import de.x4fyr.paiman.lib.services.QueryService

/** Controller for [MainView] */
open class MainViewController constructor(private val webViewService: WebViewService,
                                     private val view: MainView,
                                     private val paintingService: PaintingService,
                                     private val queryService: QueryService): Controller {

    /** See [Controller.loadView] */
    override suspend fun loadView() {
        val sb = StringBuilder()
        val models = paintingService.getFromQueryResult(queryService.allPaintingsQuery.run()).map {
            MainViewPaintingModel( it .title) }.toSet()
        view.appendTo(models, sb)
        webViewService.loadUI(sb)
    }
}

/** Model for painting previews */
data class MainViewPaintingModel(
        /** title of painting */
        val title: String)
