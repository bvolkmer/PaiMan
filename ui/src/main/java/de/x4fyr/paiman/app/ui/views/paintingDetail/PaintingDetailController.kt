package de.x4fyr.paiman.app.ui.views.paintingDetail

import de.x4fyr.paiman.app.services.PictureSelectorService
import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.Controller
import de.x4fyr.paiman.app.ui.View
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.threeten.bp.LocalDate

open class PaintingDetailController(
        private val webViewService: WebViewService,
        private val view: PaintingDetailView,
        private val returnController: Controller,
        private val pictureSelectorService: PictureSelectorService
) :
        Controller {

    init {
        view.controller = this
    }

    /** Prepare and load corresponding [View] */
    override suspend fun loadView() {
        webViewService.loadUI(view.element.await())
        webViewService.setCallbackController(this)
    }

    /** Callback: return to [returnController] */
    open fun back() {
        println("Callback: cancel()")
        launch(CommonPool) {
            returnController.loadView()
        }
    }

    /** Callback: add tag */
    open fun addTag(tag: String) {
        println("Callback: addTag(\"$tag\")")
        launch {
            if (tag.isNotBlank())
                view.model.addTag(tag)
        }
        //TODO
    }

    /** Callback: add wip */
    open fun addWIP() {
        println("Callback: addWip()")
        pictureSelectorService.pickPicture {
            launch {
                if (it != null)
                    view.model.addWip(it)
            }
        }
    }

    /** Callback: add tag */
    open fun addRef() {
        println("Callback: addRef()")
        pictureSelectorService.pickPicture {
            launch {
                if (it != null)
                    view.model.addRef(it)
            }
        }
    }

    /** Callback: finishing */
    open fun finishing(year: Int, month: Int) {
        println("Callback: finishing()")
        launch(CommonPool) {
            val date = LocalDate.of(year, month, 1)
            view.model.finishing(date)
        }
    }
}