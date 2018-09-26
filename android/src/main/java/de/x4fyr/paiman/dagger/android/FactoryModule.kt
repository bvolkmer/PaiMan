package de.x4fyr.paiman.dagger.android

import dagger.Module
import dagger.Provides
import de.x4fyr.paiman.app.adapter.Base64Encoder
import de.x4fyr.paiman.app.services.PictureSelectorService
import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.PaintingDetailControllerAndroidAdapter
import de.x4fyr.paiman.app.ui.model.PaintingDetailModelAndroidAdapter
import de.x4fyr.paiman.app.ui.views.paintingDetail.PaintingDetailFactory
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
        internal fun providePaintingDetailFactory(paintingService: PaintingService, webViewService: WebViewService, pictureSelectorService: PictureSelectorService, base64Encoder: Base64Encoder): PaintingDetailFactory {
            return PaintingDetailFactory(
                    paintingService,
                    webViewService,
                    pictureSelectorService,
                    base64Encoder,
                    { wVS, model, returnController, pSS -> PaintingDetailControllerAndroidAdapter(wVS, model, returnController, pSS) },
                    { pS, id, b64E -> PaintingDetailModelAndroidAdapter(pS, id, b64E) }
            )
        }
    }

}
