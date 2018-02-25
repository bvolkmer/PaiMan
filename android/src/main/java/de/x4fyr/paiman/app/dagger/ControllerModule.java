package de.x4fyr.paiman.app.dagger;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.services.WebViewService;
import de.x4fyr.paiman.app.ui.controller.EntryViewControllerAndroidAdapter;
import de.x4fyr.paiman.app.ui.controller.MainViewControllerAndroidAdapter;
import de.x4fyr.paiman.app.ui.views.entry.EntryController;
import de.x4fyr.paiman.app.ui.views.entry.EntryView;
import de.x4fyr.paiman.app.ui.views.overview.OverviewController;
import de.x4fyr.paiman.app.ui.views.overview.OverviewView;
import de.x4fyr.paiman.lib.services.PaintingService;
import de.x4fyr.paiman.lib.services.QueryService;

import javax.inject.Singleton;

@Module
enum ControllerModule {
    ;

    @Provides
    @Singleton
    static OverviewController provideMainViewController(WebViewService webViewService, OverviewView overviewView,
                                                        PaintingService paintingService, QueryService queryService) {
        return new MainViewControllerAndroidAdapter(webViewService, overviewView, paintingService, queryService);
    }

    @Provides
    @Singleton
    static EntryController provideEntryViewController(WebViewService webViewService, EntryView entryView,
                                                      OverviewController overviewController) {
        return new EntryViewControllerAndroidAdapter(webViewService, overviewController, entryView);
    }
}
