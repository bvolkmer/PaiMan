package de.x4fyr.paiman.dagger;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.services.WebViewService;
import de.x4fyr.paiman.app.ui.views.entry.EntryController;
import de.x4fyr.paiman.app.ui.views.entry.EntryView;
import de.x4fyr.paiman.app.ui.views.overview.OverviewController;

import javax.inject.Singleton;

@Module
enum EntryModule {
    ;

    @Provides
    @Singleton
    static EntryController provideEntryController(WebViewService webViewService, EntryView entryView,
                                                  OverviewController overviewController) {
        return new EntryController(webViewService, overviewController, entryView);
    }

    @Provides
    static EntryView provideEntryView() {
        return new EntryView();
    }
}
