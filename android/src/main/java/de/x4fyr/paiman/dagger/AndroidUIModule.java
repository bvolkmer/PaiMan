package de.x4fyr.paiman.dagger;

import dagger.Module;
import de.x4fyr.paiman.dagger.android.ControllerModule;
import de.x4fyr.paiman.dagger.android.FactoryModule;
import de.x4fyr.paiman.dagger.android.ModelModule;

@Module(includes = {ControllerModule.class, ModelModule.class, FactoryModule.class})
enum AndroidUIModule {
    ;
}
