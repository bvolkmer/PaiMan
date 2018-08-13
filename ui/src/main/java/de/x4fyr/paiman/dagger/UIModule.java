package de.x4fyr.paiman.dagger;

import dagger.Module;

@Module(includes = {EntryModule.class, OverviewModule.class, AddPaintingModule.class})
enum UIModule {
}
