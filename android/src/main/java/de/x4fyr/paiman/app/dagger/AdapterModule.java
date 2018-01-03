package de.x4fyr.paiman.app.dagger;

import android.content.Context;
import com.couchbase.lite.android.AndroidContext;
import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.adapter.JavaResourceAdapter;
import de.x4fyr.paiman.app.adapter.WebResourceAdapter;
import de.x4fyr.paiman.lib.adapter.AndroidGoogleDriveStorageAdapter;
import de.x4fyr.paiman.lib.adapter.GoogleDriveStorageAdapter;
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
    static CouchbaseAdapterImpl provideCouchbaseAdapterImpl(Context context) {
        return new CouchbaseAdapterImpl(new AndroidContext(context));
    }

    @Provides
    @Singleton
    static GoogleDriveStorageAdapter provideGoogleDriveStorageAdapter() {
        return new AndroidGoogleDriveStorageAdapter();
    }

    @Provides
    static WebResourceAdapter resourceAdapter() {
        return new JavaResourceAdapter();
    }
}
