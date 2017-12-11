package de.x4fyr.paiman.app.dagger;

import dagger.Component;
import de.x4fyr.paiman.app.services.WebViewServiceImpl;
import de.x4fyr.paiman.app.ui.controller.EntryViewController;

import javax.inject.Singleton;

@Singleton
@Component(modules = {ServiceModule.class, UIModule.class, ControllerModule.class})
public interface MainComponent{
    WebViewServiceImpl webViewServiceImpl();
    EntryViewController entryUIController();
}