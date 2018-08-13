package de.x4fyr.paiman.dagger;

import dagger.Module;
import de.x4fyr.paiman.dagger.android.AdapterModule;
import de.x4fyr.paiman.dagger.android.PlatformModule;
import de.x4fyr.paiman.dagger.android.ServiceModule;

@Module(includes = {PlatformModule.class, AdapterModule.class, ServiceModule.class})
enum AndroidModule {
}
