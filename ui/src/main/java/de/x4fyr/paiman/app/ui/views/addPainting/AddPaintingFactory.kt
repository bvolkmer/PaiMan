package de.x4fyr.paiman.app.ui.views.addPainting

import de.x4fyr.paiman.app.ui.Controller
import javax.inject.Provider

/** Factory pattern to create unique AddPainting MVC classes */
class AddPaintingFactory(private val addPaintingControllerProvider: Provider<AddPaintingController>) {


    /** Create ready to use AddPainting MVC with the [AddPaintingController] as starting point */
    fun createAddPaintingController(returnController: Controller): AddPaintingController {
        return addPaintingControllerProvider.get()
    }
}