package de.x4fyr.paiman.app.dagger;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
public class AndroidModule {

    private Context context;

    public AndroidModule(Context context) {
        this.context = context;
    }

    @Provides
    Context provideContext() {
        return context;
    }
}
