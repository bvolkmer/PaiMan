package de.x4fyr.paiman.dagger.ui;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.ui.views.addPainting.AddPaintingModel;
import de.x4fyr.paiman.app.ui.views.addPainting.AddPaintingView;
import de.x4fyr.paiman.app.ui.views.entry.EntryView;
import de.x4fyr.paiman.app.ui.views.overview.OverviewModel;
import de.x4fyr.paiman.app.ui.views.overview.OverviewView;

@Module
public enum ViewModule {
    ;


    @Provides
    static OverviewView provideOverviewView(OverviewModel overviewModel) {
        return new OverviewView(overviewModel);
    }

    @Provides
    static EntryView provideEntryView() {
        return new EntryView();
    }

    @Provides
    static AddPaintingView provideAddPaintingView(AddPaintingModel model) {
        return new AddPaintingView(model);
    }
}
