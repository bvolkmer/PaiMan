package de.x4fyr.paiman.dagger.desktop;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.lib.adapter.DesktopStorageAdapter;
import de.x4fyr.paiman.lib.adapter.StorageAdapter;

import javax.inject.Singleton;

@Module
public enum DesktopAdapterModule {
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

}
