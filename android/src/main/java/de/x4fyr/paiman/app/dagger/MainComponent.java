package de.x4fyr.paiman.app.dagger;

import dagger.Component;
import de.x4fyr.paiman.app.service.AndroidWebViewService;
import de.x4fyr.paiman.app.ui.views.entry.EntryController;

import javax.inject.Singleton;

@Singleton
@Component(modules = {ServiceModule.class, de.x4fyr.paiman.dagger.UIModule.class, de.x4fyr.paiman.dagger.ControllerModule.class, AdapterModule.class, AndroidModule.class})
public interface MainComponent {

    AndroidWebViewService webViewServiceImpl();

    EntryController entryUIController();
}