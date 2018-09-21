package de.x4fyr.paiman.app.ui.views.overview

import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.Controller
import de.x4fyr.paiman.app.ui.views.addPainting.AddPaintingFactory
import de.x4fyr.paiman.app.ui.views.paintingDetail.PaintingDetailFactory
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

/** Overview Controller */
open class OverviewController (private val webViewService: WebViewService,
                               private val addPaintingFactory: AddPaintingFactory,
                               private val model: OverviewModel,
                               private val paintingDetailFactory: PaintingDetailFactory): Controller {

    companion object {
        private const val html = "html/overview.html"
    }

    /** See [Controller.loadView] */
    override suspend fun loadView() {
        reload()
    }

    /** Reload view */
    suspend fun reload() {
        //TODO: Reload only when visible
        webViewService.loadHtml(html, this, model)
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
        //TODO
    }

    /** Callback: Open detail view of given painting by [id] */
    open fun openPainting(id: String) {
        println("Callback: openPainting($id)")
        launch (CommonPool) {
            paintingDetailFactory.createPaintingDetailController(id, this@OverviewController).loadView()
        }
    }
}