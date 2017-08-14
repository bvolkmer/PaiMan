package de.x4fyr.paiman.lib.provider

import android.content.Context
import com.couchbase.lite.android.AndroidContext
import de.x4fyr.paiman.lib.provider.ServiceProvider
import de.x4fyr.paiman.lib.services.DesignService

/**
 * The primary android implementation of the paiman [ServiceProvider].
 *
 * It also provides the platform depended [DesignService]
 */
class AndroidServiceProvider(context: Context): ServiceProvider(AndroidContext(context)) {

    /** Provided design service */
    val designService: DesignService = DesignService(context)
}