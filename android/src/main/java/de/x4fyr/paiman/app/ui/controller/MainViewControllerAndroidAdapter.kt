package de.x4fyr.paiman.app.ui.controller

import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.view.MainView
import de.x4fyr.paiman.lib.services.PaintingService
import de.x4fyr.paiman.lib.services.QueryService

/** Adapter for [MainViewController] to make functions available as javascript interface  */
class MainViewControllerAndroidAdapter(webViewService: WebViewService,
                                       view: MainView,
                                       paintingService: PaintingService,
                                       queryService: QueryService)
    : MainViewController(webViewService, view, paintingService, queryService) {

}