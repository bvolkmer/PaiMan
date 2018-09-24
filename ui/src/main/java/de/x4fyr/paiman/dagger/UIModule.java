package de.x4fyr.paiman.dagger;

import dagger.Module;
import de.x4fyr.paiman.dagger.ui.ControllerModule;
import de.x4fyr.paiman.dagger.ui.FactoryModule;
import de.x4fyr.paiman.dagger.ui.ModelModule;

@Module(includes = {ControllerModule.class, ModelModule.class, FactoryModule.class})
enum UIModule {
}
