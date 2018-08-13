package de.x4fyr.paiman.dagger;

import dagger.Module;
import de.x4fyr.paiman.dagger.android.ControllerModule;
import de.x4fyr.paiman.dagger.ui.AddPaintingModule;
import de.x4fyr.paiman.dagger.ui.ModelModule;
import de.x4fyr.paiman.dagger.ui.ViewModule;

@Module(includes = {ControllerModule.class, ViewModule.class, ModelModule.class, AddPaintingModule.class})
enum AndroidUIModule {
    ;
}
