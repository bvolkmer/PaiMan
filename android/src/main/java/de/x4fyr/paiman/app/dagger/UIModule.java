package de.x4fyr.paiman.app.dagger;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.adapter.WebResourceAdapter;
import de.x4fyr.paiman.app.ui.views.entry.EntryView;
import de.x4fyr.paiman.app.ui.views.overview.OverviewView;

@Module
enum UIModule {
    ;

    @Provides
    static OverviewView provideMainView(WebResourceAdapter resourceAdapter) {
        return new OverviewView();
    }

    @Provides
    static EntryView provideEntryView(WebResourceAdapter resourceAdapter) {
        return new EntryView();
    }
}
