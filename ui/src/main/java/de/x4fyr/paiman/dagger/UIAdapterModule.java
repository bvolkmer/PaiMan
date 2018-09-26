package de.x4fyr.paiman.dagger;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.adapter.Base64Encoder;
import de.x4fyr.paiman.app.adapter.JavaBase64Encoder;

import javax.inject.Singleton;

@Module
public enum UIAdapterModule {
    ;

    @Provides
    @Singleton
    static Base64Encoder providerBase64Encoder() {
        return new JavaBase64Encoder();
    }
}
