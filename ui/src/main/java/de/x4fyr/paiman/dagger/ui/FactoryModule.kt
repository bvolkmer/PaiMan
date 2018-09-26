package de.x4fyr.paiman.dagger.ui

import dagger.Module
import dagger.Provides
import de.x4fyr.paiman.app.adapter.Base64Encoder
import de.x4fyr.paiman.app.services.PictureSelectorService
import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.views.paintingDetail.PaintingDetailController
import de.x4fyr.paiman.app.ui.views.paintingDetail.PaintingDetailFactory
import de.x4fyr.paiman.app.ui.views.paintingDetail.PaintingDetailModel
import de.x4fyr.paiman.lib.services.PaintingService
import javax.inject.Singleton

@Module
enum class FactoryModule {
    ;


    @Module
    companion object {

        @Provides
        @Singleton
        @JvmStatic
        internal fun providePaintingDetailFactory(
                paintingService: PaintingService,
                webViewService: WebViewService,
                pictureSelectorService: PictureSelectorService,
                base64Encoder: Base64Encoder
        ): PaintingDetailFactory {
            return PaintingDetailFactory(
                    paintingService,
                    webViewService,
                    pictureSelectorService,
                    base64Encoder,
                    { wVS, model, returnController, pSS -> PaintingDetailController(wVS, model, returnController, pSS) },
                    { pS, id, b64E -> PaintingDetailModel(pS, id, b64E) }
            )

        }
    }

}
