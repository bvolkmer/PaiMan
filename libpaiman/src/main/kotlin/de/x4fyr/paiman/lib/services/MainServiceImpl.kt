package de.x4fyr.paiman.lib.services

import com.couchbase.lite.QueryEnumerator
import de.x4fyr.paiman.lib.adapter.GoogleDriveStorageAdapter
import de.x4fyr.paiman.lib.adapter.PaintingCRUDAdapter
import de.x4fyr.paiman.lib.adapter.couchbase.QueryAdapter
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
                               private val storageAdapter: GoogleDriveStorageAdapter):
        PaintingService,
        QueryService by queryAdapter {

    private val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()
    private val readLock = lock.readLock()
    private val writeLock = lock.writeLock()

    private var LOG = Logger.getLogger(this::class.simpleName)

    private val dummyPicture = Picture("dummy")

    override suspend fun get(id: String): SavedPainting {
        readLock.lock()
        val result = paintingCRUDAdapter.read(id)
        readLock.unlock()
        return result ?: throw ServiceException("Could not get painting with id $id")
    }

    override suspend fun getAll(ids: Set<String>): Set<SavedPainting> {
        readLock.lock()
        val result = paintingCRUDAdapter.read(ids)
        readLock.unlock()
        return result
    }

    override suspend fun composeNewPainting(title: String,
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
        try {
            var savedPainting = paintingCRUDAdapter.create(newPainting) ?: throw ServiceException("Could not create " +
                    "painting")
            LOG.info("Saving mainPicture to DB")
            val mainPictureID = storageAdapter.saveImage(mainPicture)
            LOG.info("Saving wips to DB")
            val wipIDs: List<String> = wip.map { storageAdapter.saveImage(it) }
            LOG.info("Saving refs to DB")
            val refIDs = reference.map { storageAdapter.saveImage(it) }
            LOG.info("Saving mainPicture, wips, refs to painting")
            savedPainting = paintingCRUDAdapter.update(painting = savedPainting.copy(
                    mainPicture = Picture(mainPictureID),
                    wip = wipIDs.map(::Picture).toSet(),
                    references = refIDs.map(::Picture).toSet()
            )) ?: throw ServiceException("Could not add pictures to painting")
            writeLock.unlock()
            return savedPainting
        } finally {
            if (writeLock.isHeldByCurrentThread) {
                writeLock.unlock()
            }
        }
    }

    override suspend fun replaceMainPicture(painting: SavedPainting,
                                            newPicture: InputStream,
                                            moveOldToWip: Boolean): SavedPainting {
        writeLock.lock()
        try {
            val mainPictureID = storageAdapter.saveImage(newPicture)
            val result = paintingCRUDAdapter.update(painting = painting.copy(
                    mainPicture = Picture(mainPictureID),
                    wip = if (moveOldToWip) painting.wip + painting.mainPicture else painting.wip))
            writeLock.unlock()
            return result ?: throw ServiceException("Could not replace main picture")
        } finally {
            if (writeLock.isHeldByCurrentThread) {
                writeLock.unlock()
            }
        }
    }

    override suspend fun changePainting(painting: SavedPainting): SavedPainting {
        writeLock.lock()
        try {
            val result = paintingCRUDAdapter.update(painting)
            writeLock.unlock()
            return result ?: throw ServiceException("Could not update painting")
        } finally {
            if (writeLock.isHeldByCurrentThread) {
                writeLock.unlock()
            }
        }
    }

    override suspend fun sellPainting(painting: SavedPainting,
                                      purchaser: Purchaser,
                                      date: LocalDate,
                                      price: Double): SavedPainting {
        writeLock.lock()
        try {
            val result = paintingCRUDAdapter.update(
                    painting.copy(sellingInfo = SellingInformation(purchaser = purchaser, date = date, price = price)))
            writeLock.unlock()
            return result ?: throw ServiceException("Could not sell painting")
        } finally {
            if (writeLock.isHeldByCurrentThread) {
                writeLock.unlock()
            }
        }

    }

    override suspend fun addWipPicture(painting: SavedPainting, images: Set<InputStream>): SavedPainting {
        writeLock.lock()
        try {
            val wipIDs = images.map { storageAdapter.saveImage(it) }
            val result = paintingCRUDAdapter.update(painting.copy(wip = painting.wip + wipIDs.map(::Picture)))
            writeLock.unlock()
            return result ?: throw ServiceException("Could not add wip")
        } finally {
            if (writeLock.isHeldByCurrentThread) {
                writeLock.unlock()
            }
        }
    }

    override suspend fun removeWipPicture(painting: SavedPainting, images: Set<String>): SavedPainting {
        writeLock.lock()
        try {
            val savedPainting = paintingCRUDAdapter.update(painting.copy(wip = painting.wip - images.map(::Picture)))
            images.forEach {
                storageAdapter.deleteImage(it)
            }
            writeLock.unlock()
            return savedPainting ?: throw ServiceException("Could not remove wip")
        } finally {
            if (writeLock.isHeldByCurrentThread) {
                writeLock.unlock()
            }
        }
    }

    override suspend fun addReferences(painting: SavedPainting,
                                       references: Set<InputStream>): SavedPainting {
        writeLock.lock()
        try {
            val refIDs = references.map { storageAdapter.saveImage(it) }
            val result = paintingCRUDAdapter.update(
                    painting.copy(references = painting.references + refIDs.map(::Picture)))
            writeLock.unlock()
            return result ?: throw ServiceException("Could not add reference")
        } finally {
            if (writeLock.isHeldByCurrentThread) {
                writeLock.unlock()
            }
        }
    }

    override suspend fun removeReferences(painting: SavedPainting, references: Set<String>): SavedPainting {
        writeLock.lock()
        try {
            val savedPainting = paintingCRUDAdapter.update(
                    painting.copy(references = painting.references - references.map(::Picture)))
            references.forEach {
                storageAdapter.deleteImage(it)
            }
            writeLock.unlock()
            return savedPainting ?: throw ServiceException("Could not remove painting")
        } finally {
            if (writeLock.isHeldByCurrentThread) {
                writeLock.unlock()
            }
        }
    }

    override suspend fun addTags(painting: SavedPainting, tags: Set<String>): SavedPainting {
        writeLock.lock()
        try {
            val result = paintingCRUDAdapter.update(painting.copy(tags = painting.tags + tags))
            writeLock.unlock()
            return result ?: throw ServiceException("Could not add tags")
        } finally {
            if (writeLock.isHeldByCurrentThread) {
                writeLock.unlock()
            }
        }
    }

    override suspend fun removeTags(painting: SavedPainting, tags: Set<String>): SavedPainting {
        writeLock.lock()
        try {
            val result = paintingCRUDAdapter.update(painting.copy(tags = painting.tags - tags))
            writeLock.unlock()
            return result ?: throw ServiceException("Could not remove tags")
        } finally {
            if (writeLock.isHeldByCurrentThread) {
                writeLock.unlock()
            }
        }
    }

    override suspend fun getPictureStream(picture: Picture): InputStream {
        readLock.lock()
        try {
            val result = storageAdapter.getImage(picture.id)
            readLock.unlock()
            return result
        } catch (e: Exception) {
            readLock.unlock()
            throw e
        }
    }

    override suspend fun getFromQueryResult(queryEnumerator: QueryEnumerator): Set<SavedPainting> = getAll(
            queryEnumerator.map { it.key.toString() }.toSet())

    override suspend fun delete(painting: SavedPainting) {
        writeLock.lock()
        try {
            storageAdapter.deleteImage(painting.mainPicture.id)
            painting.wip.forEach { storageAdapter.deleteImage(it.id) }
            painting.references.forEach { storageAdapter.deleteImage(it.id) }
            paintingCRUDAdapter.delete(id = painting.id)
            writeLock.unlock()
        } finally {
            if (writeLock.isHeldByCurrentThread) {
                writeLock.unlock()
            }
        }
    }

    override suspend fun delete(paintingId: String) {
        writeLock.lock()
        try {
            paintingCRUDAdapter.delete(id = paintingId)
            writeLock.unlock()
        } finally {
            if (writeLock.isHeldByCurrentThread) {
                writeLock.unlock()
            }
        }
    }
}