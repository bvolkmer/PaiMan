package de.x4fyr.paiman.dagger.android;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
public class PlatformModule {

    private final Context context;

    public PlatformModule(Context context) {
        this.context = context;
    }

    @Provides
    Context provideContext() {
        return context;
    }
}
