package de.x4fyr.paiman.dagger;

import dagger.Component;
import de.x4fyr.paiman.app.service.AndroidWebViewService;
import de.x4fyr.paiman.app.ui.views.entry.EntryController;

import javax.inject.Singleton;

@Singleton
@Component(modules = {LibPaimanModule.class, AndroidUIModule.class, AndroidModule.class})
public interface MainComponent {

    AndroidWebViewService webViewServiceImpl();

    EntryController entryUIController();
}