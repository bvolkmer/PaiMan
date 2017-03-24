package de.x4fyr.paiman.lib.adapter.database

import de.x4fyr.paiman.lib.domain.*
import kotlinx.coroutines.experimental.Deferred
import java.time.LocalDate
import java.util.concurrent.Future

/**
 * @author de.x4fyr
 * Created on 3/4/17.
 */
internal interface PaintingCRUDAdapter {
    fun create(painting: UnsavedPainting): SavedPainting?
    fun read(id: String): SavedPainting?
    fun read(indices: Set<String>): Set<SavedPainting>
    fun update(painting: SavedPainting): SavedPainting?
    fun delete(id: String): Boolean
}