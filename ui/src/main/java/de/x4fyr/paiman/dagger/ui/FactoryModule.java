package de.x4fyr.paiman.dagger.ui;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.ui.views.addPainting.AddPaintingController;
import de.x4fyr.paiman.app.ui.views.addPainting.AddPaintingFactory;

import javax.inject.Provider;
import javax.inject.Singleton;

@Module
public enum FactoryModule {
    ;

    @Provides
    @Singleton
    static AddPaintingFactory provideAddPaintingFactory(Provider<AddPaintingController> provider) {
        return new AddPaintingFactory(provider);
    }
}
