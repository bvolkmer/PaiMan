package de.x4fyr.paiman.app.ui.views.paintingDetail

import de.x4fyr.paiman.app.services.PictureSelectorService
import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.Controller
import de.x4fyr.paiman.lib.services.PaintingService

class PaintingDetailFactory(
        private val paintingService: PaintingService,
        private val webViewService: WebViewService,
        private val pictureSelectorService: PictureSelectorService
) {

    fun createPaintingDetailController(id: String, returnController: Controller): PaintingDetailController {
        val model = PaintingDetailModel(paintingService, id)
        val view = PaintingDetailView(model)
        return PaintingDetailController(webViewService, view, returnController, pictureSelectorService)
    }
}