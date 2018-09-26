package de.x4fyr.paiman.dagger.android;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.WebViewWrapperActivity;

@Module
public class PlatformModule {

    private final WebViewWrapperActivity activity;

    public PlatformModule(WebViewWrapperActivity activity) {
        this.activity = activity;
    }

    @Provides
    Context provideContext() {
        return activity;
    }

    @Provides
    WebViewWrapperActivity provideWebViewWrapperActivity() {
        return activity;
    }
}
