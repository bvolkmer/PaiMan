package de.x4fyr.paiman.app.ui.controller

import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.views.addPainting.AddPaintingFactory
import de.x4fyr.paiman.app.ui.views.overview.OverviewController
import de.x4fyr.paiman.app.ui.views.overview.OverviewModel
import de.x4fyr.paiman.app.ui.views.overview.OverviewView
import de.x4fyr.paiman.lib.services.PaintingService
import de.x4fyr.paiman.lib.services.QueryService

/** Adapter for [OverviewController] to make functions available as javascript interface  */
class MainViewControllerAndroidAdapter(webViewService: WebViewService,
                                       view: OverviewView,
                                       model: OverviewModel,
                                       addPaintingFactory: AddPaintingFactory)
    : OverviewController(webViewService, view, addPaintingFactory, model) {

}