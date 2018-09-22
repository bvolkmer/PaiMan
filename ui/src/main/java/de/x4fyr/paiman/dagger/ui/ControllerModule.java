package de.x4fyr.paiman.dagger.ui;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.services.PictureSelectorService;
import de.x4fyr.paiman.app.services.WebViewService;
import de.x4fyr.paiman.app.ui.views.addPainting.AddPaintingController;
import de.x4fyr.paiman.app.ui.views.addPainting.AddPaintingFactory;
import de.x4fyr.paiman.app.ui.views.addPainting.AddPaintingView;
import de.x4fyr.paiman.app.ui.views.entry.EntryController;
import de.x4fyr.paiman.app.ui.views.overview.OverviewController;
import de.x4fyr.paiman.app.ui.views.overview.OverviewModel;
import de.x4fyr.paiman.app.ui.views.paintingDetail.PaintingDetailFactory;

import javax.inject.Singleton;

@Module
public enum ControllerModule {
    ;

    @Provides
    @Singleton
    static OverviewController provideOverviewController(WebViewService webViewService, AddPaintingFactory addPaintingFactory, OverviewModel overviewModel, PaintingDetailFactory paintingDetailFactory, PictureSelectorService pictureSelectorService) {
        return new OverviewController(webViewService, addPaintingFactory, overviewModel, paintingDetailFactory, pictureSelectorService);
    }

    @Provides
    @Singleton
    static EntryController provideEntryController(WebViewService webViewService,
                                                  OverviewController overviewController) {
        return new EntryController(webViewService, overviewController);
    }

    @Provides
    static AddPaintingController provideAddPaintingController(AddPaintingView view, WebViewService webViewService, PictureSelectorService pictureSelectorService) {
        return new AddPaintingController(view, webViewService, pictureSelectorService);
    }
}
