package de.x4fyr.paiman.app.dagger;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.services.WebViewService;
import de.x4fyr.paiman.app.ui.controller.EntryViewController;
import de.x4fyr.paiman.app.ui.controller.MainViewController;
import de.x4fyr.paiman.app.ui.view.EntryView;
import de.x4fyr.paiman.app.ui.view.MainView;

import javax.inject.Singleton;

@Module
public class ControllerModule {

    @Provides
    @Singleton
    static MainViewController provideMainViewController(WebViewService webViewService, MainView mainView) {
        return new MainViewController(webViewService, mainView);
    }

    @Provides
    @Singleton
    static EntryViewController provideEntryViewController(WebViewService webViewService, EntryView entryView,
                                                          MainViewController mainViewController) {
        return new EntryViewController(webViewService, mainViewController, entryView);
    }
}
