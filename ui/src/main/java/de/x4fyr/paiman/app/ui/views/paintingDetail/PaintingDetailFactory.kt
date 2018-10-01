package de.x4fyr.paiman.app.ui.views.paintingDetail

import de.x4fyr.paiman.app.adapter.Base64Encoder
import de.x4fyr.paiman.app.services.PictureSelectorService
import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.Controller
import de.x4fyr.paiman.lib.services.PaintingService

class PaintingDetailFactory(
        private val paintingService: PaintingService,
        private val webViewService: WebViewService,
        private val pictureSelectorService: PictureSelectorService,
        private val base64Encoder: Base64Encoder,
        private val controllerFactory: (WebViewService, PaintingDetailModel, Controller, PictureSelectorService) -> PaintingDetailController,
        private val modelFactory: (PaintingService, String, Base64Encoder) -> PaintingDetailModel) {

    fun createPaintingDetailController(id: String, returnController: Controller): PaintingDetailController {
        val model = modelFactory(paintingService, id, base64Encoder)
        return controllerFactory(webViewService, model, returnController, pictureSelectorService)
    }
}