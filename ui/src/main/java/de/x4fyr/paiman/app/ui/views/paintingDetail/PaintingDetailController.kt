package de.x4fyr.paiman.app.ui.views.paintingDetail

import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.Controller

class PaintingDetailController(private val webViewService: WebViewService, private val view: PaintingDetailView,
                               private val model: PaintingDetailModel):
        Controller {
    /** Prepare and load corresponding [View] */
    suspend override fun loadView() {
        TODO("not implemented") //TODO: not implemented
    }
}