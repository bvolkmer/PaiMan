package de.x4fyr.paiman.dagger;

import dagger.Component;
import de.x4fyr.paiman.app.services.JavaFxWebViewService;
import de.x4fyr.paiman.app.ui.views.entry.EntryController;

import javax.inject.Singleton;

@Singleton
@Component(modules = {DesktopModule.class, LibPaimanModule.class, UIModule.class})
public interface MainComponent {
    JavaFxWebViewService webViewServiceImpl();

    EntryController entryController();
}