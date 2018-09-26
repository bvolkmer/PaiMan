package de.x4fyr.paiman.dagger.android;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.adapter.Base64Encoder;
import de.x4fyr.paiman.app.ui.model.OverviewModelAndroidAdapter;
import de.x4fyr.paiman.app.ui.views.overview.OverviewModel;
import de.x4fyr.paiman.lib.services.PaintingService;
import de.x4fyr.paiman.lib.services.QueryService;

import javax.inject.Singleton;

@Module
public enum  ModelModule {
    ;

    @Provides
    @Singleton
    static OverviewModel providesOverviewModel(PaintingService paintingService, QueryService queryService, Base64Encoder base64Encoder) {
        return new OverviewModelAndroidAdapter(paintingService, queryService, base64Encoder);
    }

}
