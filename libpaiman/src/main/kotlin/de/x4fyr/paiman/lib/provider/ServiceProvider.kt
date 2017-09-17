package de.x4fyr.paiman.lib.provider

import com.couchbase.lite.Context
import de.x4fyr.paiman.lib.adapter.GoogleDriveStorageAdapter
import de.x4fyr.paiman.lib.adapter.couchbase.CouchbaseAdapterImpl
import de.x4fyr.paiman.lib.services.MainServiceImpl
import de.x4fyr.paiman.lib.services.PaintingService
import de.x4fyr.paiman.lib.services.QueryService

/**
 * @author de.x4fyr
 * Created on 3/4/17.
 */
abstract class ServiceProvider(context: Context, storageAdapter: GoogleDriveStorageAdapter) {
    private val couchbaseAdapterImpl = CouchbaseAdapterImpl(context)
    private val paintingServiceImpl = MainServiceImpl(couchbaseAdapterImpl, couchbaseAdapterImpl, storageAdapter)
    /** [PaintingService] instance */
    val paintingService: PaintingService = paintingServiceImpl
    /** [QueryService] instance */
    val queryService: QueryService = paintingServiceImpl

}