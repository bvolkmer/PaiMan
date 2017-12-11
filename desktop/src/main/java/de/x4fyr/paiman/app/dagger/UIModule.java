package de.x4fyr.paiman.app.dagger;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.ui.view.EntryView;
import de.x4fyr.paiman.app.ui.view.MainView;

@Module
public class UIModule {

    @Provides
    static MainView provideMainView() {
        return new MainView();
    }

    @Provides
    static EntryView provideEntryView() {
        return new EntryView();
    }
}
