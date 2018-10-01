package de.x4fyr.paiman.dagger;

import dagger.Module;
import de.x4fyr.paiman.dagger.desktop.DesktopAdapterModule;
import de.x4fyr.paiman.dagger.desktop.DesktopServiceModule;

@Module(includes = {DesktopServiceModule.class, DesktopAdapterModule.class})
enum DesktopModule {
}
