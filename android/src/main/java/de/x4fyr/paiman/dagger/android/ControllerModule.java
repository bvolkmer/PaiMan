package de.x4fyr.paiman.dagger.android;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.services.WebViewService;
import de.x4fyr.paiman.app.ui.controller.EntryViewControllerAndroidAdapter;
import de.x4fyr.paiman.app.ui.controller.MainViewControllerAndroidAdapter;
import de.x4fyr.paiman.app.ui.views.entry.EntryController;
import de.x4fyr.paiman.app.ui.views.overview.OverviewController;
import de.x4fyr.paiman.app.ui.views.overview.OverviewModel;

import javax.inject.Singleton;

@Module
public enum ControllerModule {
    ;

    @Provides
    @Singleton
    static OverviewController provideMainViewController(WebViewService webViewService,
                                                        OverviewModel model) {
        return new MainViewControllerAndroidAdapter(webViewService, model);
    }

    @Provides
    @Singleton
    static EntryController provideEntryViewController(WebViewService webViewService,
                                                      OverviewController overviewController) {
        return new EntryViewControllerAndroidAdapter(webViewService, overviewController);
    }
}
