@file:Suppress("KDocMissingDocumentation")

package de.x4fyr.paiman

import android.content.Context
import dagger.*
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import de.x4fyr.paiman.lib.adapter.AndroidGoogleDriveStorageAdapter
import de.x4fyr.paiman.lib.provider.AndroidServiceProvider
import de.x4fyr.paiman.lib.provider.ServiceProvider
import de.x4fyr.paiman.lib.services.DesignService
import de.x4fyr.paiman.lib.services.PaintingService
import de.x4fyr.paiman.lib.services.QueryService
import javax.inject.Scope
import javax.inject.Singleton

/**
 * Main App [Component]
 */
@Component(modules = arrayOf(
        AndroidSupportInjectionModule::class,
        ActivitiesModule::class,
        ApplicationModule::class, ServicesModule::class))
@Singleton
interface AppComponent: AndroidInjector<DaggerApplication> {
    fun inject(application: Application)

    override fun inject(instance: DaggerApplication?)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: android.app.Application): AppComponent.Builder

        fun build(): AppComponent
    }

}

@Module
abstract class ApplicationModule {
    @Binds
    abstract fun bindContext(application: android.app.Application): Context
}

@Module
abstract class ActivitiesModule {

    @ContributesAndroidInjector(modules = arrayOf())
    abstract fun contributeMainActivityInjector(): MainActivity

    @ContributesAndroidInjector(modules = arrayOf())
    abstract fun contributeDriveTestActivity(): DriveTestActivity
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope


@Singleton
@Module
class ServicesModule {

    private lateinit var storageAdapter: AndroidGoogleDriveStorageAdapter
    private lateinit var provider: AndroidServiceProvider

    @Provides
    @Singleton
    fun provideAndroidGoogleDriveStorageAdapter(context: android.app.Application): AndroidGoogleDriveStorageAdapter
            = try {
        storageAdapter
    } catch (e: UninitializedPropertyAccessException) {
        storageAdapter = AndroidGoogleDriveStorageAdapter(context)
        storageAdapter
    }

    @Provides
    @Singleton
    fun provideAndroidServiceProvider(context: android.app.Application,
                                      storageAdapter: AndroidGoogleDriveStorageAdapter): AndroidServiceProvider = try {
        provider
    } catch (e: UninitializedPropertyAccessException) {
        provider = AndroidServiceProvider(context, storageAdapter)
        provider
    }

    @Provides
    @Singleton
    fun provideServiceProvider(provider: AndroidServiceProvider): ServiceProvider = provider

    @Provides
    @Singleton
    fun providePaintingService(provider: ServiceProvider): PaintingService = provider.paintingService

    @Provides
    @Singleton
    fun provideQueryService(provider: ServiceProvider): QueryService = provider.queryService

    @Provides
    @Singleton
    fun provideDesignService(provider: AndroidServiceProvider): DesignService = provider.designService
}