package de.x4fyr.paiman.app.dagger;

import dagger.Component;
import de.x4fyr.paiman.app.services.JavaFxWebViewService;
import de.x4fyr.paiman.app.ui.controller.EntryViewController;

import javax.inject.Singleton;

@Singleton
@Component(modules = {ServiceModule.class, UIModule.class, ControllerModule.class, AdapterModule.class})
public interface MainComponent{
    JavaFxWebViewService webViewServiceImpl();
    EntryViewController entryUIController();
}