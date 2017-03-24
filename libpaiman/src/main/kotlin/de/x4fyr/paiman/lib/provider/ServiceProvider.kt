package de.x4fyr.paiman.lib.provider

import com.couchbase.lite.Context
import de.x4fyr.paiman.lib.adapter.database.CouchbaseAdapterImpl
import de.x4fyr.paiman.lib.services.MainServiceImpl
import de.x4fyr.paiman.lib.services.PaintingService
import de.x4fyr.paiman.lib.services.QueryService

/**
 * @author de.x4fyr
 * Created on 3/4/17.
 */
abstract class ServiceProvider(context: Context) {

    private val couchbaseAdapterImpl = CouchbaseAdapterImpl(context)
    private val paintingServiceImpl = MainServiceImpl(couchbaseAdapterImpl, couchbaseAdapterImpl, couchbaseAdapterImpl)
    val paintingService: PaintingService = paintingServiceImpl
    val queryService: QueryService = paintingServiceImpl

}