package de.x4fyr.paiman.app.dagger;

import dagger.Component;
import de.x4fyr.paiman.app.service.AndroidWebViewService;
import de.x4fyr.paiman.app.ui.controller.EntryViewController;

import javax.inject.Singleton;

@Singleton
@Component(modules = {ServiceModule.class, UIModule.class, ControllerModule.class, AdapterModule.class, AndroidModule.class})
public interface MainComponent {

    AndroidWebViewService webViewServiceImpl();

    EntryViewController entryUIController();
}