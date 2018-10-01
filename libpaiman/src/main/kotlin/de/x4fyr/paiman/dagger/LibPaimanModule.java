package de.x4fyr.paiman.dagger;

import dagger.Module;

@Module(includes = {AdapterModule.class, ServiceModule.class})
enum LibPaimanModule {
}
