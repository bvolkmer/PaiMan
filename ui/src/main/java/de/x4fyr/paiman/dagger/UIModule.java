package de.x4fyr.paiman.dagger;

import dagger.Module;
import de.x4fyr.paiman.dagger.ui.ControllerModule;
import de.x4fyr.paiman.dagger.ui.FactoryModule;
import de.x4fyr.paiman.dagger.ui.ModelModule;
import de.x4fyr.paiman.dagger.ui.ViewModule;

@Module(includes = {ControllerModule.class, ViewModule.class, ModelModule.class, FactoryModule.class})
enum UIModule {
}
