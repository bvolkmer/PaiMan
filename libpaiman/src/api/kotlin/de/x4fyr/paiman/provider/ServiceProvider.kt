package de.x4fyr.paiman.provider

import de.x4fyr.paiman.adapter.DatabaseAdapter
import de.x4fyr.paiman.services.PaintingService

/**
 * @author x4fyr
 * Created on 3/4/17.
 */
interface ServiceProvider {

    fun provideDatabaseService(): DatabaseAdapter

    fun providePaintingService(): PaintingService
}