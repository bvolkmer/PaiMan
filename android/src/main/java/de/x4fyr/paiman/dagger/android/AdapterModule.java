package de.x4fyr.paiman.dagger.android;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.adapter.AndroidBase64Encoder;
import de.x4fyr.paiman.app.adapter.Base64Encoder;
import de.x4fyr.paiman.lib.adapter.AndroidStorageAdapter;
import de.x4fyr.paiman.lib.adapter.StorageAdapter;

import javax.inject.Singleton;

@Module
public enum AdapterModule {
    ;


    @Provides
    @Singleton
    static StorageAdapter provideStorageAdapter(Context context) {
        return new AndroidStorageAdapter(context);
    }

    @Provides
    @Singleton
    static Base64Encoder provideBase64Encoder() {
        return new AndroidBase64Encoder();
    }
}
