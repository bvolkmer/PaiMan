package de.x4fyr.paiman.app.ui.views.addPainting

import de.x4fyr.paiman.app.services.PictureSelectorService
import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.Controller
import de.x4fyr.paiman.lib.services.PaintingService

/** Factory pattern to create unique AddPainting MVC classes */
class AddPaintingFactory(private val webViewService: WebViewService,
                         private val pictureSelectorService: PictureSelectorService,
                         private val paintingService: PaintingService) {

    /** Create ready to use AddPainting MVC with the [AddPaintingController] as starting point */
    fun createAddPaintingController(returnController: Controller): AddPaintingController {
        val model = AddPaintingModel(paintingService)
        val view = AddPaintingView(model)
        model.addObserver(view)
        val controller = AddPaintingController(view, model, returnController, webViewService, pictureSelectorService)
        view.controller = controller
        return controller
    }
}