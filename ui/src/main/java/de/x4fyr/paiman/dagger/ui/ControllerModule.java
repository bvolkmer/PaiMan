package de.x4fyr.paiman.dagger.ui;

import dagger.Provides;
import dagger.Module;
import de.x4fyr.paiman.app.services.WebViewService;
import de.x4fyr.paiman.app.ui.views.addPainting.AddPaintingFactory;
import de.x4fyr.paiman.app.ui.views.entry.EntryController;
import de.x4fyr.paiman.app.ui.views.entry.EntryView;
import de.x4fyr.paiman.app.ui.views.overview.OverviewController;
import de.x4fyr.paiman.app.ui.views.overview.OverviewModel;
import de.x4fyr.paiman.app.ui.views.overview.OverviewView;

import javax.inject.Singleton;

@Module
public enum ControllerModule {
    ;

    @Provides
    @Singleton
    static OverviewController provideOverviewController(WebViewService webViewService, OverviewView overviewView,
                                                        AddPaintingFactory addPaintingFactory, OverviewModel overviewModel) {
        return new OverviewController(webViewService, overviewView, addPaintingFactory, overviewModel);
    }

    @Provides
    @Singleton
    static EntryController provideEntryController(WebViewService webViewService, EntryView entryView,
                                                  OverviewController overviewController) {
        return new EntryController(webViewService, overviewController, entryView);
    }
}
