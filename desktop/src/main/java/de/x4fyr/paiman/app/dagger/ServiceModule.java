package de.x4fyr.paiman.app.dagger;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.services.JavaFxWebViewService;
import de.x4fyr.paiman.app.services.WebViewService;
import de.x4fyr.paiman.lib.adapter.GoogleDriveStorageAdapter;
import de.x4fyr.paiman.lib.adapter.PaintingCRUDAdapter;
import de.x4fyr.paiman.lib.adapter.couchbase.QueryAdapter;
import de.x4fyr.paiman.lib.services.MainServiceImpl;
import de.x4fyr.paiman.lib.services.PaintingService;
import de.x4fyr.paiman.lib.services.QueryService;

import javax.inject.Singleton;

@Module
enum ServiceModule {
    ;

    @Provides
    @Singleton
    static WebViewService provideWebViewService(JavaFxWebViewService javaFxWebViewService) {
        return javaFxWebViewService;
    }

    @Provides
    @Singleton
    static JavaFxWebViewService provideJavaFxWebViewService() {
        return new JavaFxWebViewService();
    }

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
                                                  GoogleDriveStorageAdapter googleDriveStorageAdapter) {
        return new MainServiceImpl(paintingCRUDAdapter, queryAdapter, googleDriveStorageAdapter);
    }

}