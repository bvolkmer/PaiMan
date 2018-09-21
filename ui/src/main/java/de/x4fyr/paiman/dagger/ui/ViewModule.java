package de.x4fyr.paiman.dagger.ui;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.ui.views.addPainting.AddPaintingModel;
import de.x4fyr.paiman.app.ui.views.addPainting.AddPaintingView;

@Module
public enum ViewModule {
    ;


    @Provides
    static AddPaintingView provideAddPaintingView(AddPaintingModel model) {
        return new AddPaintingView(model);
    }
}
