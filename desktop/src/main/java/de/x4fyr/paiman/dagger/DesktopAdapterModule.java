package de.x4fyr.paiman.dagger;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.adapter.JavaResourceAdapter;
import de.x4fyr.paiman.app.adapter.WebResourceAdapter;
import de.x4fyr.paiman.lib.adapter.DesktopStorageAdapter;
import de.x4fyr.paiman.lib.adapter.StorageAdapter;

import javax.inject.Singleton;

@Module
enum DesktopAdapterModule {
    ;

    @Provides
    @Singleton
    static DesktopStorageAdapter provideDesktopStorageAdapter() {
        return new DesktopStorageAdapter();
    }

    @Provides
    @Singleton
    static StorageAdapter provideStorageAdapter(DesktopStorageAdapter storageAdapter) {
        return storageAdapter;
    }

    @Provides
    static WebResourceAdapter provideResourceAdapter() {
        return new JavaResourceAdapter();
    }
}
