package de.x4fyr.paiman.lib.provider

import android.content.Context
import com.couchbase.lite.android.AndroidContext
import de.x4fyr.paiman.lib.adapter.AndroidGoogleDriveStorageAdapter
import de.x4fyr.paiman.lib.services.DesignService

/**
 * The primary android implementation of the paiman [ServiceProvider].
 *
 * It also provides the platform depended [DesignService]
 */
class AndroidServiceProvider(context: Context, storageAdapter: AndroidGoogleDriveStorageAdapter): ServiceProvider(
        AndroidContext(context), storageAdapter) {

    /** Provided design service */
    val designService: DesignService = DesignService(context)
}