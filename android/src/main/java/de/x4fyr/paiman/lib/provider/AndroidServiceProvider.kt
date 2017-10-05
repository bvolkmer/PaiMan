package de.x4fyr.paiman.lib.provider

import android.content.Context
import com.couchbase.lite.android.AndroidContext
import de.x4fyr.paiman.lib.adapter.AndroidGoogleDriveStorageAdapter
import de.x4fyr.paiman.lib.services.DesignService
import javax.inject.Singleton

/**
 * The primary android implementation of the paiman [ServiceProvider].
 *
 * It also provides the platform depended [DesignService]
 */
@Singleton
class AndroidServiceProvider(context: Context, storageAdapter: AndroidGoogleDriveStorageAdapter): ServiceProvider(
        AndroidContext(context), storageAdapter)
