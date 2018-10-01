package de.x4fyr.paiman.dagger.android;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.WebViewWrapperActivity;
import de.x4fyr.paiman.app.service.AndroidPictureSelectorService;
import de.x4fyr.paiman.app.service.AndroidWebViewService;
import de.x4fyr.paiman.app.services.PictureSelectorService;
import de.x4fyr.paiman.app.services.WebViewService;

import javax.inject.Singleton;

@Module
public enum ServiceModule {
    ;

    @Provides
    @Singleton
    static WebViewService provideWebViewService(AndroidWebViewService webViewService) {
        return webViewService;
    }

    @Provides
    @Singleton
    static AndroidWebViewService provideWebViewServiceImpl(Context context) {
        return new AndroidWebViewService(context);
    }

    @Provides
    static PictureSelectorService providePictureSelectorService(WebViewWrapperActivity activity) {
        return new AndroidPictureSelectorService(activity);
    }

}