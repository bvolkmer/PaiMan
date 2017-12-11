package de.x4fyr.paiman.app.dagger;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.services.WebViewService;
import de.x4fyr.paiman.app.services.WebViewServiceImpl;

import javax.inject.Singleton;

@Module
public class ServiceModule {

    public ServiceModule() {}

    @Provides
    @Singleton
    static WebViewService provideWebViewService(WebViewServiceImpl webViewServiceImpl) {
        return webViewServiceImpl;
    }

    @Provides
    @Singleton
    static WebViewServiceImpl provideWebViewServiceImpl() {
        return new WebViewServiceImpl();
    }

}