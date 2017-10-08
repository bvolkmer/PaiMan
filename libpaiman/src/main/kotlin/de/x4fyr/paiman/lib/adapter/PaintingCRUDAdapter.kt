package de.x4fyr.paiman.lib.adapter

import de.x4fyr.paiman.lib.domain.SavedPainting
import de.x4fyr.paiman.lib.domain.UnsavedPainting

/**
 * @author de.x4fyr
 * Created on 3/4/17.
 */
internal interface PaintingCRUDAdapter {

    /** Create a painting entity */
    fun create(painting: UnsavedPainting): SavedPainting?
    /** Read a painting entity */
    fun read(id: String): SavedPainting?
    /** Read multiple painting entities] */
    fun read(indices: Set<String>): Set<SavedPainting>
    /** Update a painting entity */
    fun update(painting: SavedPainting): SavedPainting?
    /** Delete a painting entity */
    fun delete(id: String): Boolean
}