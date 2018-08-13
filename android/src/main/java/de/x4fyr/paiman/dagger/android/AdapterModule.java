package de.x4fyr.paiman.dagger.android;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.adapter.JavaResourceAdapter;
import de.x4fyr.paiman.app.adapter.WebResourceAdapter;
import de.x4fyr.paiman.lib.adapter.AndroidGoogleDriveStorageAdapter;
import de.x4fyr.paiman.lib.adapter.GoogleDriveStorageAdapter;
import de.x4fyr.paiman.lib.adapter.StorageAdapter;

import javax.inject.Singleton;

@Module
public enum AdapterModule {
    ;


    @Provides
    @Singleton
    static GoogleDriveStorageAdapter provideGoogleDriveStorageAdapter() {
        return new AndroidGoogleDriveStorageAdapter();
    }

    @Provides
    @Singleton
    static StorageAdapter provideStorageAdapter(GoogleDriveStorageAdapter googleDriveStorageAdapter) {
        return googleDriveStorageAdapter;
    }

    @Provides
    static WebResourceAdapter resourceAdapter() {
        return new JavaResourceAdapter();
    }
}
