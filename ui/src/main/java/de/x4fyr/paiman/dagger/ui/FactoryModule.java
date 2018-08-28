package de.x4fyr.paiman.dagger.ui;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.services.PictureSelectorService;
import de.x4fyr.paiman.app.services.WebViewService;
import de.x4fyr.paiman.app.ui.views.addPainting.AddPaintingController;
import de.x4fyr.paiman.app.ui.views.addPainting.AddPaintingFactory;
import de.x4fyr.paiman.app.ui.views.paintingDetail.PaintingDetailFactory;
import de.x4fyr.paiman.lib.services.PaintingService;

import javax.inject.Provider;
import javax.inject.Singleton;

@Module
public enum FactoryModule {
    ;

    @Provides
    @Singleton
    static AddPaintingFactory provideAddPaintingFactory(Provider<AddPaintingController> provider) {
        return new AddPaintingFactory(provider);
    }

    @Provides
    @Singleton
    static PaintingDetailFactory providePaintingDetailFactory(PaintingService paintingService, WebViewService webViewService, PictureSelectorService pictureSelectorService) {
        return new PaintingDetailFactory(paintingService, webViewService, pictureSelectorService);
    }

}
