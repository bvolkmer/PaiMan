package de.x4fyr.paiman

import android.app.Activity
import android.support.multidex.MultiDexApplication
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 * A template for any Activity used in this app needed for dependency injection
 */
class Application: MultiDexApplication(), HasActivityInjector {

    /** Injected by dagger */
    @Inject lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    /** Returns an [AndroidInjector] of [Activity]s.  */
    override fun activityInjector(): AndroidInjector<Activity> = dispatchingActivityInjector

    /** See [MultiDexApplication] complemented with dependency injection */
    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder().application(this).build().inject(this)
    }
}