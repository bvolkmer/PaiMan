package de.x4fyr.paiman.app.provider

import de.x4fyr.paiman.lib.adapter.DatabaseAdapter
import de.x4fyr.paiman.lib.adapter.DummyDatabaseAdapter
import de.x4fyr.paiman.lib.provider.ServiceProvider
import de.x4fyr.paiman.lib.services.PaintingService
import de.x4fyr.paiman.lib.services.PaintingServiceImpl

/**
 * @author x4fyr
 * Created on 3/17/17.
 */
class AndroidServiceProvider : ServiceProvider {
    val databaseAdapter: DatabaseAdapter by lazy { DummyDatabaseAdapter() }
    override val paintingService: PaintingService by lazy { PaintingServiceImpl(databaseAdapter) }


}