package de.x4fyr.paiman.dagger.desktop;

import dagger.Module;
import dagger.Provides;
import de.x4fyr.paiman.app.services.JavaFxPictureSelectorService;
import de.x4fyr.paiman.app.services.JavaFxWebViewService;
import de.x4fyr.paiman.app.services.PictureSelectorService;
import de.x4fyr.paiman.app.services.WebViewService;
import javafx.stage.Stage;

import javax.inject.Singleton;

@Module
public class DesktopServiceModule {

    private final Stage stage;

    public DesktopServiceModule(Stage stage) {
        this.stage = stage;
    }

    @Provides
    @Singleton
    Stage provideStage(){
        return stage;
    }


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
    PictureSelectorService providePictureSelectorService(Stage stage) {
        return new JavaFxPictureSelectorService(stage);
    }

}
