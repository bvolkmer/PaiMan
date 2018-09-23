package de.x4fyr.paiman.app.ui.controller

import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.views.overview.OverviewController
import de.x4fyr.paiman.app.ui.views.overview.OverviewModel

/** Adapter for [OverviewController] to make functions available as javascript interface  */
class MainViewControllerAndroidAdapter(webViewService: WebViewService,
                                       model: OverviewModel,
                                       )
    : OverviewController(webViewService, model) {

}