package de.x4fyr.paiman.app.ui.views.addPainting

import de.x4fyr.paiman.app.services.PictureSelectorService
import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.Controller
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

/** Controller for AddPainting MVC */
open class AddPaintingController(private val addPaintingView: AddPaintingView,
                                 private val webViewService: WebViewService,
                                 private val pictureSelectorService: PictureSelectorService): Controller {

    private val model = addPaintingView.model
    var returnController: Controller? = null

    init {
        addPaintingView.controller = this
    }

    suspend override fun loadView() {
        reload()
    }

    /** Reload [addPaintingView] with [webViewService] */
    suspend fun reload() {
        webViewService.loadUI(addPaintingView.element.await())
        webViewService.setCallbackController(this)
    }

    /** Callback: cancel adding and return to [returnController] */
    open fun cancel() {
        println("Callback: cancel()")
        launch(CommonPool) {
            returnController!!.loadView()
        }
    }

    /** Callback: Open dialog to select a picture and save it to [model] */
    open fun addPicture() {
        println("Callback: addPicture()")
        pictureSelectorService.pickPicture {
            if (it != null) model.setPicture(it)
        }
    }

    /** Callback: Set title in [model] */
    open fun setTitle(title: String) {
        model.title = title
    }

    /** Callback: Save painting in [model] and return to [returnController] on success */
    open fun save() {
        model.save(onSuccess = {
            launch(CommonPool) {
                returnController!!.loadView()
            }
        }, onFailure = {
            /*TODO: Add error handling*/
            println("Error saving new painting: ${it.message}\n${it.stackTrace}")
        })
    }
}