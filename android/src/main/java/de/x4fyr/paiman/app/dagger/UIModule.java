package de.x4fyr.paiman.app.dagger;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.adapter.WebResourceAdapter;
import de.x4fyr.paiman.app.ui.view.EntryView;
import de.x4fyr.paiman.app.ui.view.MainView;

@Module
enum UIModule {
    ;

    @Provides
    static MainView provideMainView(WebResourceAdapter resourceAdapter) {
        return new MainView(resourceAdapter);
    }

    @Provides
    static EntryView provideEntryView(WebResourceAdapter resourceAdapter) {
        return new EntryView(resourceAdapter);
    }
}
