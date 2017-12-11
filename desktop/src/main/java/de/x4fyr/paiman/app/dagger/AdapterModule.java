package de.x4fyr.paiman.app.dagger;

import com.couchbase.lite.JavaContext;
import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.lib.adapter.GoogleDriveStorageAdapter;
import de.x4fyr.paiman.lib.adapter.JavaGDriveAdapterImpl;
import de.x4fyr.paiman.lib.adapter.PaintingCRUDAdapter;
import de.x4fyr.paiman.lib.adapter.couchbase.CouchbaseAdapterImpl;
import de.x4fyr.paiman.lib.adapter.couchbase.QueryAdapter;

import javax.inject.Singleton;

@Module
enum AdapterModule {
    ;

    @Provides
    @Singleton
    static PaintingCRUDAdapter providePaintingCRUDAdapter(CouchbaseAdapterImpl couchbaseAdapter) {
        return couchbaseAdapter;
    }

    @Provides
    @Singleton
    static QueryAdapter provideQueryAdapter(CouchbaseAdapterImpl couchbaseAdapter) {
        return couchbaseAdapter;
    }

    @Provides
    @Singleton
    static CouchbaseAdapterImpl provideCouchbaseAdapterImpl() {
        return new CouchbaseAdapterImpl(new JavaContext());
    }

    @Provides
    @Singleton
    static GoogleDriveStorageAdapter provideGoogleDriveStorageAdapter() {
        return new JavaGDriveAdapterImpl();
    }
}
