package de.x4fyr.paiman.lib.provider

import de.x4fyr.paiman.lib.adapter.DatabaseAdapter
import de.x4fyr.paiman.lib.services.PaintingService

/**
 * @author de.x4fyr
 * Created on 3/4/17.
 */
interface ServiceProvider {

    val paintingService: PaintingService

}