package de.x4fyr.paiman.lib.adapter.database

import com.couchbase.lite.*
import de.x4fyr.paiman.lib.domain.Painting
import de.x4fyr.paiman.lib.domain.SavedPainting
import de.x4fyr.paiman.lib.domain.UnsavedPainting
import java.io.InputStream
import java.util.UUID
import java.util.logging.Logger


/**
 * Created by x4fyr on 3/17/17.
 */
internal class CouchbaseAdapterImpl(context: Context) : PaintingCRUDAdapter, PictureCRUDAdapter, QueryAdapter {

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
                setMap(Mapper { document, emitter ->
                    if (document[TYPE].toString() == TYPES.PAINTING) emitter.emit(document[ID], document[ID])
                }, "1.0")
            }
        }
    }

    override val allPaintingsQuery: Query by lazy {
        vAllPaintings.createQuery()
    }

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

    override fun createPicture(pictureStream: InputStream, paintingID: String): String {
        val markableStream: InputStream = if (pictureStream.markSupported()) pictureStream
        else pictureStream.buffered(pictureStream.available())
        markableStream.mark(Int.MAX_VALUE)
        val id = UUID.nameUUIDFromBytes(markableStream.readBytes()).toString()
        markableStream.reset()
        database.getExistingDocument(paintingID)?.currentRevision?.createRevision()?.apply {
            LOG.info("Saving attachment with a size of: ${markableStream.available()}")
            setAttachment(id, "image", markableStream)
            save()
        }
        return id
    }

    override fun readPicture(id: String, paintingID: String): InputStream? = database
            .getExistingDocument(paintingID)?.currentRevision?.getAttachment(id)?.content

    override fun readPictures(ids: Set<String>, paintingID: String): Set<InputStream> = ids.map {
        readPicture(it, paintingID)
    }.filterNotNull().toSet()

    override fun updatePicture(id: String, pictureStream: InputStream, paintingID: String) {
        database.getExistingDocument(paintingID)?.currentRevision?.createRevision()?.apply {
            setAttachment(id, "image", pictureStream)
            save()
        }
    }

    override fun deletePicture(id: String, paintingID: String) {
        database.getExistingDocument(paintingID).currentRevision.createRevision().apply {
            removeAttachment(id)
            save()
        }
    }

}
