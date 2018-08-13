package de.x4fyr.paiman.app.ui.views.overview

import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.Controller
import de.x4fyr.paiman.app.ui.views.addPainting.AddPaintingFactory
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

/** Controller for [OverviewView] */
open class OverviewController (private val webViewService: WebViewService,
                               private val view: OverviewView,
                               private val addPaintingFactory: AddPaintingFactory,
                               private val model: OverviewModel ): Controller {

    /** See [Controller.loadView] */
    override suspend fun loadView() {
        view.controller = this
        reload()
    }

    /** Reload view */
    suspend fun reload() {
        //TODO: Reload only when visible
        webViewService.loadUI(view.element.await())
        webViewService.setCallbackController(this)
    }

    /** Callback: Open add painting dialog */
    open fun openAddPainting() {
        println("Callback: openAddPainting()")
        launch(CommonPool) {
            addPaintingFactory.createAddPaintingController(this@OverviewController).loadView()
        }
    }

    /** Callback: Refresh previews */
    open fun refresh() {
        println("Callback: refresh()")
        //TODO: Replace forced view update
        view.update(model, null)
    }

    /** Callback: Open detail view of given painting by [id] */
    open fun openPainting(id: String) {
        println("Callback: openPainting($id)")
        //TODO: Open painting detail view
    }
}