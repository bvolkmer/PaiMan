package de.x4fyr.paiman.app.ui.views.overview

import de.x4fyr.paiman.app.services.PictureSelectorService
import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.Controller
import de.x4fyr.paiman.app.ui.views.paintingDetail.PaintingDetailFactory
import kotlinx.coroutines.experimental.*

/** Overview Controller */
open class OverviewController(private val webViewService: WebViewService,
                              private val model: OverviewModel,
                              private val paintingDetailFactory: PaintingDetailFactory,
                              private val pictureSelectorService: PictureSelectorService) : Controller {

    companion object {
        private const val html = "html/overview.htm"
    }

    /** See [Controller.loadView] */
    override suspend fun loadView() {
        reload()
    }

    /** Reload view */
    private suspend fun reload() {
        //TODO: Reload only when visible
        webViewService.loadHtml(html, this, model)
    }

    /** Callback: Refresh previews */
    open fun refresh() {
        println("Callback: refresh()")
        webViewService.executeJS("refreshPreviews()")
    }

    /** Callback: Open detail view of given painting by [id] */
    open fun openPainting(id: String) {
        println("Callback: openPainting($id)")
        launch(CommonPool) {
            paintingDetailFactory.createPaintingDetailController(id, this@OverviewController).loadView()
        }
    }

    open fun addPainting(title: String?, month: String?, year: String?) {
        println("Callback: addPainting($title)")
        if (title.isNullOrBlank()) {
            webViewService.showError("Title missing or invalid")
        } else if (model.addPaintingModel.image == null) {
            webViewService.showError("Image missing")
        } else if (month.isNullOrBlank()) {
            webViewService.showError("Month invalid")
        } else if (year.isNullOrBlank()) {
            webViewService.showError("Year invalid")
        } else {
            val monthInt = month!!.toIntOrNull()
            val yearInt = year!!.toIntOrNull()
            if (monthInt == null || monthInt < 1 || monthInt > 12) {
                webViewService.showError("Month invalid")
            } else if (yearInt == null) {
                webViewService.showError("Year invalid")
            } else launch(CommonPool) {
                model.addPaintingModel.title = title
                model.addPaintingModel.month = monthInt
                model.addPaintingModel.year = yearInt
                val newId = model.saveNewPainting()
                if (newId != null) {
                    paintingDetailFactory.createPaintingDetailController(newId, this@OverviewController).loadView()
                } else {
                    println("Error: Tried to save unfinished painting")
                }
            }
        }
    }

    open fun selectImage() {
        println("Callback: selectImage()")
        launch(CommonPool) {
            pictureSelectorService.pickPicture {
                if (it != null) {
                    val jpegData = model.addPaintingModel.setImage(it)
                    println("selected image")
                    webViewService.executeJS("addDialogSetPicture('$jpegData')")
                } else {
                    webViewService.showError("Couldn\\'t get image")
                }
            }
        }
    }
}