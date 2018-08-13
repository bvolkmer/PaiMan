package de.x4fyr.paiman.dagger;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.lib.adapter.PaintingCRUDAdapter;
import de.x4fyr.paiman.lib.adapter.StorageAdapter;
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
    static CouchbaseAdapterImpl provideCouchbaseAdapterImpl(StorageAdapter storageAdapter) {
        return new CouchbaseAdapterImpl(storageAdapter);
    }
}
