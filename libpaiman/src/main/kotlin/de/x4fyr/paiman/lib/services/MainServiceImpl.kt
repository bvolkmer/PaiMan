package de.x4fyr.paiman.lib.services

import com.couchbase.lite.QueryEnumerator
import de.x4fyr.paiman.lib.adapter.database.PaintingCRUDAdapter
import de.x4fyr.paiman.lib.adapter.database.PictureCRUDAdapter
import de.x4fyr.paiman.lib.adapter.database.QueryAdapter
import de.x4fyr.paiman.lib.domain.*
import org.threeten.bp.LocalDate
import java.io.InputStream
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.logging.Logger

/**
 * Main Service implementation combining multiple services
 * @author de.x4fyr
 * Created on 3/1/17.
 */
internal class MainServiceImpl(private var paintingCRUDAdapter: PaintingCRUDAdapter,
                               queryAdapter: QueryAdapter,
                               private val pictureCRUDAdapter: PictureCRUDAdapter):
        PaintingService,
        QueryService by queryAdapter {

    private val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()
    private val readLock = lock.readLock()
    private val writeLock = lock.writeLock()

    private var LOG = Logger.getLogger(this::class.simpleName)

    private val dummyPicture = Picture("dummy")

    override fun get(id: String): SavedPainting {
        readLock.lock()
        val result = paintingCRUDAdapter.read(id)
        readLock.unlock()
        return result ?: throw ServiceException("Could not get")
    }

    override fun getAll(ids: Set<String>): Set<SavedPainting> {
        readLock.lock()
        val result = paintingCRUDAdapter.read(ids)
        readLock.unlock()
        return result
    }

    override fun composeNewPainting(title: String,
                                    mainPicture: InputStream,
                                    wip: Set<InputStream>,
                                    reference: Set<InputStream>,
                                    sellingInfo: SellingInformation?,
                                    tags: Set<String>): SavedPainting {
        val newPainting = UnsavedPainting(
                title = title,
                wip = setOf(),
                references = setOf(),
                sellingInfo = sellingInfo,
                tags = tags, mainPicture = dummyPicture)
        writeLock.lock()
        var savedPainting = paintingCRUDAdapter.create(newPainting) ?: throw ServiceException("Could not create " +
                "painting")
        LOG.info("Saving mainPicture to DB")
        val mainPictureID = pictureCRUDAdapter.createPicture(pictureStream = mainPicture, paintingID = savedPainting.id)
        LOG.info("Saving wips to DB")
        val wipIDs = wip.map { pictureCRUDAdapter.createPicture(it, savedPainting.id) }
        LOG.info("Saving refs to DB")
        val refIDs = reference.map { pictureCRUDAdapter.createPicture(it, savedPainting.id) }
        LOG.info("Saving mainPicture, wips, refs to painting")
        savedPainting = paintingCRUDAdapter.update(painting = savedPainting.copy(
                mainPicture = Picture(mainPictureID),
                wip = wipIDs.map(::Picture).toSet(),
                references = refIDs.map(::Picture).toSet()
        )) ?: throw ServiceException("Could not add pictures to painting")
        writeLock.unlock()
        return savedPainting
    }

    override fun replaceMainPicture(painting: SavedPainting,
                                    newPicture: InputStream,
                                    moveOldToWip: Boolean): SavedPainting {
        writeLock.lock()
        val mainPictureID = pictureCRUDAdapter.createPicture(newPicture, paintingID = painting.id)
        val result = paintingCRUDAdapter.update(painting = painting.copy(
                mainPicture = Picture(mainPictureID),
                wip = if (moveOldToWip) painting.wip + painting.mainPicture else painting.wip))
        writeLock.unlock()
        return result ?: throw ServiceException("Could not replace main picture")
    }

    override fun changePainting(painting: SavedPainting): SavedPainting {
        writeLock.lock()
        val result = paintingCRUDAdapter.update(painting)
        writeLock.unlock()
        return result ?: throw ServiceException("Could not update painting")
    }

    override fun sellPainting(painting: SavedPainting,
                              purchaser: Purchaser,
                              date: LocalDate,
                              price: Double): SavedPainting {
        writeLock.lock()
        val result = paintingCRUDAdapter.update(
                painting.copy(sellingInfo = SellingInformation(purchaser = purchaser, date = date, price = price)))
        writeLock.unlock()
        return result ?: throw ServiceException("Could not sell painting")

    }

    override fun addWipPicture(painting: SavedPainting, images: Set<InputStream>): SavedPainting {
        writeLock.lock()
        val wipIDs = images.map { pictureCRUDAdapter.createPicture(pictureStream = it, paintingID = painting.id) }
        val result = paintingCRUDAdapter.update(painting.copy(wip = painting.wip + wipIDs.map(::Picture)))
        writeLock.unlock()
        return result ?: throw ServiceException("Could not add wip")
    }

    override fun removeWipPicture(painting: SavedPainting, images: Set<String>): SavedPainting {
        writeLock.lock()
        val savedPainting = paintingCRUDAdapter.update(painting.copy(wip = painting.wip - images.map(::Picture)))
        images.forEach { pictureCRUDAdapter.deletePicture(id = it, paintingID = painting.id) }
        writeLock.unlock()
        return savedPainting ?: throw ServiceException("Could not remove wip")
    }

    override fun addReferences(painting: SavedPainting,
                               references: Set<InputStream>): SavedPainting {
        writeLock.lock()
        val refIDs = references.map { pictureCRUDAdapter.createPicture(pictureStream = it, paintingID = painting.id) }
        val result = paintingCRUDAdapter.update(painting.copy(references = painting.references + refIDs.map(::Picture)))
        writeLock.unlock()
        return result ?: throw ServiceException("Could not add reference")
    }

    override fun removeReferences(painting: SavedPainting, references: Set<String>): SavedPainting {
        writeLock.lock()
        val savedPainting = paintingCRUDAdapter.update(
                painting.copy(references = painting.references - references.map(::Picture)))
        references.forEach { pictureCRUDAdapter.deletePicture(id = it, paintingID = painting.id) }
        writeLock.unlock()
        return savedPainting ?: throw ServiceException("Could not remove painting")
    }

    override fun addTags(painting: SavedPainting, tags: Set<String>): SavedPainting {
        writeLock.lock()
        val result = paintingCRUDAdapter.update(painting.copy(tags = painting.tags + tags))
        writeLock.unlock()
        return result ?: throw ServiceException("Could not add tags")
    }

    override fun removeTags(painting: SavedPainting, tags: Set<String>): SavedPainting {
        writeLock.lock()
        val result = paintingCRUDAdapter.update(painting.copy(tags = painting.tags - tags))
        writeLock.unlock()
        return result ?: throw ServiceException("Could not remove tags")
    }

    override fun getPictureStream(picture: Picture, painting: SavedPainting): InputStream {
        readLock.lock()
        val result = pictureCRUDAdapter.readPicture(id = picture.id, paintingID = painting.id)
        readLock.unlock()
        return result ?: throw ServiceException("Could not get picture")
    }

    override fun getAllPictureStreams(pictures: Set<Picture>, painting: SavedPainting): Set<InputStream> {
        readLock.lock()
        val result = pictureCRUDAdapter.readPictures(pictures.map { it.id }.toSet(), painting.id)
        readLock.lock()
        return result
    }

    override fun getFromQueryResult(queryEnumerator: QueryEnumerator): Set<SavedPainting> = getAll(
            queryEnumerator.map { it.key.toString() }.toSet())

    override fun delete(painting: SavedPainting) {
        writeLock.lock()
        paintingCRUDAdapter.delete(id = painting.id)
        writeLock.unlock()
    }

    override fun delete(paintingId: String) {
        writeLock.lock()
        paintingCRUDAdapter.delete(id = paintingId)
        writeLock.unlock()
    }
}