package de.x4fyr.paiman.dagger.ui;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.ui.views.overview.OverviewModel;
import de.x4fyr.paiman.lib.services.PaintingService;
import de.x4fyr.paiman.lib.services.QueryService;

@Module
public enum ModelModule {
    ;

    @Provides
    static OverviewModel provideOverviewModel(PaintingService paintingService, QueryService queryService) {
        return new OverviewModel(paintingService, queryService);
    }

}
