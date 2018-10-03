package de.x4fyr.paiman.app.ui

import android.webkit.JavascriptInterface
import de.x4fyr.paiman.app.services.PictureSelectorService
import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.views.entry.EntryController
import de.x4fyr.paiman.app.ui.views.overview.OverviewController
import de.x4fyr.paiman.app.ui.views.overview.OverviewModel
import de.x4fyr.paiman.app.ui.views.paintingDetail.PaintingDetailController
import de.x4fyr.paiman.app.ui.views.paintingDetail.PaintingDetailFactory
import de.x4fyr.paiman.app.ui.views.paintingDetail.PaintingDetailModel

class PaintingDetailControllerAndroidAdapter(
        webViewService: WebViewService,
        model: PaintingDetailModel,
        returnController: Controller,
        pictureSelectorService: PictureSelectorService
) : PaintingDetailController( webViewService, model, returnController, pictureSelectorService ) {

    /** Callback: return to [returnController] */
    @JavascriptInterface
    override fun back() = super.back()

    /** Callback: add tag */
    @JavascriptInterface
    override fun addTag(tag: String) = super.addTag(tag)

    /** Callback: add wip */
    @JavascriptInterface
    override fun addWIP() = super.addWIP()

    /** Callback: add ref */
    @JavascriptInterface
    override fun addRef() = super.addRef()

    /** Callback: finishing */
    @JavascriptInterface
    override fun finishing(year: Int, month: Int) = super.finishing(year, month)
}

/** Adapter for [OverviewController] to make functions available as javascript interface  */
class OverviewControllerAndroidAdapter(private val webViewService: WebViewService,
                                       private val model: OverviewModel,
                                       private val paintingDetailFactory: PaintingDetailFactory,
                                       private val pictureSelectorService: PictureSelectorService)
    : OverviewController(webViewService, model, paintingDetailFactory, pictureSelectorService) {

    /** Callback: Refresh previews */
    @JavascriptInterface
    override fun refresh() = super.refresh()

    /** Callback: Open detail view of given painting by [id] */
    @JavascriptInterface
    override fun openPainting(id: String) = super.openPainting(id)

    @JavascriptInterface
    override fun addPainting(title: String?, month: String?, year: String?) = super.addPainting(title, month, year)

    @JavascriptInterface
    override fun selectImage() = super.selectImage()
}

/** Adapter for [EntryController] to make functions available as javascript interface  */
class EntryControllerAndroidAdapter(webViewService: WebViewService, mainViewController: OverviewController): EntryController(webViewService, mainViewController) {

}