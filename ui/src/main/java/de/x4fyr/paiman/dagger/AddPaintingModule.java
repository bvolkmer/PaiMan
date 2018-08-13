package de.x4fyr.paiman.dagger;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.services.PictureSelectorService;
import de.x4fyr.paiman.app.services.WebViewService;
import de.x4fyr.paiman.app.ui.views.addPainting.AddPaintingFactory;
import de.x4fyr.paiman.lib.services.PaintingService;

import javax.inject.Singleton;


@Module
enum AddPaintingModule {
    ;

    @Provides
    @Singleton
    static AddPaintingFactory provideAddPaintingFactory(WebViewService webViewService,
                                                        PictureSelectorService pictureSelectorService,
                                                        PaintingService paintingService) {
        return new AddPaintingFactory(webViewService, pictureSelectorService, paintingService);
    }

}
