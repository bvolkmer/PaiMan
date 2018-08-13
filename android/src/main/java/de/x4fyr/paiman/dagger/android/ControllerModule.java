package de.x4fyr.paiman.dagger.android;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.services.WebViewService;
import de.x4fyr.paiman.app.ui.controller.EntryViewControllerAndroidAdapter;
import de.x4fyr.paiman.app.ui.controller.MainViewControllerAndroidAdapter;
import de.x4fyr.paiman.app.ui.views.addPainting.AddPaintingFactory;
import de.x4fyr.paiman.app.ui.views.entry.EntryController;
import de.x4fyr.paiman.app.ui.views.entry.EntryView;
import de.x4fyr.paiman.app.ui.views.overview.OverviewController;
import de.x4fyr.paiman.app.ui.views.overview.OverviewModel;
import de.x4fyr.paiman.app.ui.views.overview.OverviewView;
import de.x4fyr.paiman.lib.services.PaintingService;
import de.x4fyr.paiman.lib.services.QueryService;

import javax.inject.Singleton;

@Module
public enum ControllerModule {
    ;

    @Provides
    @Singleton
    static OverviewController provideMainViewController(WebViewService webViewService, OverviewView overviewView,
                                                        OverviewModel model, AddPaintingFactory addPaintingFactory) {
        return new MainViewControllerAndroidAdapter(webViewService, overviewView, model, addPaintingFactory);
    }

    @Provides
    @Singleton
    static EntryController provideEntryViewController(WebViewService webViewService, EntryView entryView,
                                                      OverviewController overviewController) {
        return new EntryViewControllerAndroidAdapter(webViewService, overviewController, entryView);
    }
}
