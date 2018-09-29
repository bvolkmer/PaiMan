package de.x4fyr.paiman.app.ui.model

import android.webkit.JavascriptInterface
import de.x4fyr.paiman.app.adapter.Base64Encoder
import de.x4fyr.paiman.app.ui.views.overview.OverviewModel
import de.x4fyr.paiman.app.ui.views.paintingDetail.PaintingDetailModel
import de.x4fyr.paiman.lib.services.PaintingService
import de.x4fyr.paiman.lib.services.QueryService

class OverviewModelAndroidAdapter(
        paintingService: PaintingService,
        queryService: QueryService,
        base64Encoder: Base64Encoder
) : OverviewModel(paintingService, queryService, base64Encoder) {
    @JavascriptInterface
    override fun getPreviews(): String = super.getPreviews()
}

class PaintingDetailModelAndroidAdapter(
        paintingService: PaintingService,
        id: String,
        base64Encoder: Base64Encoder
) : PaintingDetailModel(paintingService, id, base64Encoder) {
    @JavascriptInterface
    override fun getHolder(): String = super.getHolder()
}