package de.x4fyr.paiman.dagger.android;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.services.PictureSelectorService;
import de.x4fyr.paiman.app.services.WebViewService;
import de.x4fyr.paiman.app.ui.EntryControllerAndroidAdapter;
import de.x4fyr.paiman.app.ui.OverviewControllerAndroidAdapter;
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
    static OverviewController provideOverviewController(WebViewService webViewService, OverviewModel overviewModel, PaintingDetailFactory paintingDetailFactory, PictureSelectorService pictureSelectorService) {
        return new OverviewControllerAndroidAdapter(webViewService, overviewModel, paintingDetailFactory, pictureSelectorService);
    }

    @Provides
    @Singleton
    static EntryController provideEntryController(WebViewService webViewService,
                                                  OverviewController overviewController) {
        return new EntryControllerAndroidAdapter(webViewService, overviewController);
    }
}
