package de.x4fyr.paiman.dagger;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.services.WebViewService;
import de.x4fyr.paiman.app.ui.views.addPainting.AddPaintingFactory;
import de.x4fyr.paiman.app.ui.views.overview.OverviewController;
import de.x4fyr.paiman.app.ui.views.overview.OverviewModel;
import de.x4fyr.paiman.app.ui.views.overview.OverviewView;
import de.x4fyr.paiman.lib.services.PaintingService;
import de.x4fyr.paiman.lib.services.QueryService;

import javax.inject.Singleton;

@Module
enum OverviewModule {
    ;


    @Provides
    @Singleton
    static OverviewController provideOverviewController(WebViewService webViewService, OverviewView overviewView,
                                                        AddPaintingFactory addPaintingFactory, OverviewModel overviewModel) {
        return new OverviewController(webViewService, overviewView, addPaintingFactory, overviewModel);
    }

    @Provides
    static OverviewView provideOverviewView(OverviewModel overviewModel) {
        return new OverviewView(overviewModel);
    }

    @Provides
    static OverviewModel provideOverviewModel(PaintingService paintingService, QueryService queryService) {
        return new OverviewModel(paintingService, queryService);
    }
}
