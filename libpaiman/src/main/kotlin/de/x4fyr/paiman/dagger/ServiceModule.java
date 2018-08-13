package de.x4fyr.paiman.dagger;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.lib.adapter.PaintingCRUDAdapter;
import de.x4fyr.paiman.lib.adapter.StorageAdapter;
import de.x4fyr.paiman.lib.adapter.couchbase.QueryAdapter;
import de.x4fyr.paiman.lib.services.MainServiceImpl;
import de.x4fyr.paiman.lib.services.PaintingService;
import de.x4fyr.paiman.lib.services.QueryService;

import javax.inject.Singleton;

@Module
class ServiceModule {

    @Provides
    @Singleton
    static PaintingService providePaintingService(MainServiceImpl mainServiceImpl) {
        return mainServiceImpl;
    }

    @Provides
    @Singleton
    static QueryService provideQueryService(MainServiceImpl mainService) {
        return mainService;
    }

    @Provides
    @Singleton
    static MainServiceImpl provideMainServiceImpl(PaintingCRUDAdapter paintingCRUDAdapter,
                                                  QueryAdapter queryAdapter,
                                                  StorageAdapter storageAdapter) {
        return new MainServiceImpl(paintingCRUDAdapter, queryAdapter, storageAdapter);
    }

}