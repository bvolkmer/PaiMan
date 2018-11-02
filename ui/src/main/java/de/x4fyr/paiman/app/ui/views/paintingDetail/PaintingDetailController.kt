package de.x4fyr.paiman.app.ui.views.paintingDetail

import de.x4fyr.paiman.app.services.PictureSelectorService
import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.Controller
import de.x4fyr.paiman.app.ui.View
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

open class PaintingDetailController(
        private val webViewService: WebViewService,
        private val model: PaintingDetailModel,
        private val returnController: Controller,
        private val pictureSelectorService: PictureSelectorService
) :
        Controller {

    /** Prepare and load corresponding [View] */
    override suspend fun loadView() {
        webViewService.loadHtml("html/detail.html", this, model)
    }

    /** Callback: return to [returnController] */
    open fun back() {
        println("Callback: back()")
        GlobalScope.launch {
            returnController.loadView()
        }
    }

    /** Callback: add tag */
    open fun addTag(tag: String) {
        println("Callback: addTag(\"$tag\")")
        GlobalScope.launch {
            if (tag.isNotBlank()) {
                model.addTag(tag)
                webViewService.executeJS("refreshModel()")
            }
        }
    }

    /** Callback: add wip */
    open fun addWIP() {
        println("Callback: addWip()")
        pictureSelectorService.pickPicture {
            GlobalScope.launch {
                if (it != null) {
                    model.addWip(it)
                    webViewService.executeJS("refreshModel()")
                }
            }
        }
    }

    /** Callback: add ref */
    open fun addRef() {
        println("Callback: addRef()")
        pictureSelectorService.pickPicture {
            GlobalScope.launch {
                if (it != null) {
                    model.addRef(it)
                    webViewService.executeJS("refreshModel()")
                }
            }
        }
    }

    /** Callback: finishing */
    open fun finishing(year: Int, month: Int) {
        println("Callback: finishing()")
        GlobalScope.launch {
            val date = LocalDate.of(year, month, 1)
            model.finishing(date)
            //TODO: Update view
        }
    }

    /** Callback: delete */
    open fun delete() {
        println("Callback: finishing()")
        GlobalScope.launch {
            model.delete()
            returnController.loadView()
        }
    }
}