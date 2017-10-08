package de.x4fyr.paiman.lib.adapter.couchbase

import com.couchbase.lite.*
import de.x4fyr.paiman.lib.adapter.PaintingCRUDAdapter
import de.x4fyr.paiman.lib.domain.SavedPainting
import de.x4fyr.paiman.lib.domain.UnsavedPainting
import java.util.logging.Logger


/**
 * Couchbase Adapter for [PaintingCRUDAdapter] and [QueryAdapter]
 */
internal class CouchbaseAdapterImpl(context: Context) : PaintingCRUDAdapter, QueryAdapter {

    private val LOG = Logger.getLogger(this::class.simpleName)
    private val manager: Manager = Manager(context, Manager.DEFAULT_OPTIONS)
    private val database: Database = try {
        manager.getDatabase(DB_NAME)
    } catch (e: CouchbaseLiteException) {
        throw e
    }

    private val vAllPaintings: View = database.getView("allPaintings").apply {
        database.getView("allPaintings").apply {
            if (map == null) {
                setMap({ document, emitter ->
                    if (document[TYPE].toString() == TYPES.PAINTING) emitter.emit(document[ID], document[ID])
                }, "1.0")
            }
        }
    }

    /** See [QueryAdapter.allPaintingsQuery] */
    override val allPaintingsQuery: Query by lazy {
        vAllPaintings.createQuery()
    }

    /** See [QueryAdapter.allQuery] */
    override val allQuery: Query by lazy {
        database.createAllDocumentsQuery()
    }

    override fun create(painting: UnsavedPainting): SavedPainting? = SavedPainting(
            database.createDocument().apply {
                putProperties(painting.toPropertiesMap().toMutableMap().apply { remove(ID) })
            }?.properties)

    override fun read(id: String): SavedPainting? = SavedPainting(database.getExistingDocument(id)?.properties)

    override fun read(indices: Set<String>): Set<SavedPainting> = indices.map { read(it) }.filterNotNull().toSet()

    override fun update(painting: SavedPainting): SavedPainting? = SavedPainting(
            database.getExistingDocument(painting.id)?.update { newRev ->
                newRev.userProperties = painting.toPropertiesMap().toMutableMap().apply { remove(ID) }
                true
            }?.properties)

    override fun delete(id: String) = database.getExistingDocument(id)?.delete() ?: false

}
