package de.x4fyr.paiman.lib.services

import com.couchbase.lite.QueryEnumerator
import de.x4fyr.paiman.lib.adapter.PaintingCRUDAdapter
import de.x4fyr.paiman.lib.adapter.StorageAdapter
import de.x4fyr.paiman.lib.adapter.couchbase.QueryAdapter
import de.x4fyr.paiman.lib.domain.*
import org.threeten.bp.LocalDate
import java.io.InputStream
import java.util.logging.Logger

/**
 * Main Service implementation combining multiple services
 * @author de.x4fyr
 * Created on 3/1/17.
 */
class MainServiceImpl(private var paintingCRUDAdapter: PaintingCRUDAdapter,
                      queryAdapter: QueryAdapter,
                      private val storageAdapter: StorageAdapter):
        PaintingService,
        QueryService by queryAdapter {
    private var LOG = Logger.getLogger(this::class.simpleName)

    private val dummyPicture = Picture("dummy")

    override suspend fun get(id: String): SavedPainting = paintingCRUDAdapter.read(id) ?: throw ServiceException(
            "Could not get painting with id $id")

    override suspend fun getAll(ids: Set<String>): Set<SavedPainting> = paintingCRUDAdapter.read(ids)

    @Synchronized
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
        val savedPainting = paintingCRUDAdapter.create(newPainting) ?: throw ServiceException(
                "Could not create " +
                        "painting")
        LOG.info("Saving mainPicture to DB")
        val mainPictureID = try {
            storageAdapter.saveImage(mainPicture)
        } catch (e: StorageAdapter.StorageException) {
            throw ServiceException("Failed to save main image", e)
        }
        LOG.info("Saving wips to DB")
        val wipIDs: List<String> = wip.map {
            try {
                storageAdapter.saveImage(it)
            } catch (e: StorageAdapter.StorageException) {
                throw ServiceException("Failed to save wip images", e)
            }
        }
        LOG.info("Saving refs to DB")
        val refIDs = reference.map {
            try {
                storageAdapter.saveImage(it)
            } catch (e: StorageAdapter.StorageException) {
                throw ServiceException("Failed to save reference images", e)
            }
        }
        LOG.info("Saving mainPicture, wips, refs to painting")
        return paintingCRUDAdapter.update(painting = savedPainting.copy(
                mainPicture = Picture(mainPictureID),
                wip = wipIDs.map(::Picture).toSet(),
                references = refIDs.map(::Picture).toSet()
        )) ?: throw ServiceException("Could not add pictures to painting")

    }

    @Synchronized
    override suspend fun replaceMainPicture(painting: SavedPainting,
                                            newPicture: InputStream,
                                            moveOldToWip: Boolean): SavedPainting {
        val mainPictureID = try {
            storageAdapter.saveImage(newPicture)
        } catch (e: StorageAdapter.StorageException) {
            throw ServiceException("Failed to save main image", e)
        }
        return paintingCRUDAdapter.update(painting = painting.copy(
                mainPicture = Picture(mainPictureID),
                wip = if (moveOldToWip) painting.wip + painting.mainPicture else painting.wip))
                ?: throw ServiceException("Could not replace main picture")
    }

    @Synchronized
    override suspend fun changePainting(painting: SavedPainting): SavedPainting =
            paintingCRUDAdapter.update(painting) ?: throw ServiceException("Could not update painting")

    @Synchronized
    override suspend fun sellPainting(painting: SavedPainting,
                                      purchaser: Purchaser,
                                      date: LocalDate,
                                      price: Double): SavedPainting = paintingCRUDAdapter.update(painting.copy(
            sellingInfo = SellingInformation(purchaser = purchaser, date = date, price = price)))
            ?: throw ServiceException("Could not sell painting")

    @Synchronized
    override suspend fun addWipPicture(painting: SavedPainting, images: Set<InputStream>): SavedPainting {
        val wipIDs = images.map {
            try {
                storageAdapter.saveImage(it)
            } catch (e: StorageAdapter.StorageException) {
                throw ServiceException("Failed to save wip images", e)
            }
        }
        return paintingCRUDAdapter.update(painting.copy(wip = painting.wip + wipIDs.map(::Picture)))
                ?: throw ServiceException("Could not add wip")
    }

    @Synchronized
    override suspend fun removeWipPicture(painting: SavedPainting, images: Set<String>): SavedPainting {
        val savedPainting = paintingCRUDAdapter.update(
                painting.copy(wip = painting.wip - images.map(::Picture)))
        images.forEach { storageAdapter.deleteImage(it) }
        return savedPainting ?: throw ServiceException("Could not remove wip")
    }

    @Synchronized
    override suspend fun addReferences(painting: SavedPainting, references: Set<InputStream>): SavedPainting {
        val refIDs = references.map {
            try {
                storageAdapter.saveImage(it)
            } catch (e: StorageAdapter.StorageException) {
                throw ServiceException("Failed to save reference images", e)
            }
        }
        return paintingCRUDAdapter.update(painting.copy(references = painting.references + refIDs.map(::Picture)))
                ?: throw ServiceException("Could not add reference")
    }

    @Synchronized
    override suspend fun removeReferences(painting: SavedPainting, references: Set<String>): SavedPainting {
        val savedPainting = paintingCRUDAdapter.update(
                painting.copy(references = painting.references - references.map(::Picture)))
        references.forEach { storageAdapter.deleteImage(it) }
        return savedPainting ?: throw ServiceException("Could not remove painting")
    }

    @Synchronized
    override suspend fun addTags(painting: SavedPainting, tags: Set<String>): SavedPainting =
            paintingCRUDAdapter.update(painting.copy(tags = painting.tags + tags))
                    ?: throw ServiceException("Could not add tags")

    @Synchronized
    override suspend fun removeTags(painting: SavedPainting, tags: Set<String>): SavedPainting =
            paintingCRUDAdapter.update(painting.copy(tags = painting.tags - tags))
                    ?: throw ServiceException("Could not remove tags")

    override suspend fun getPictureStream(picture: Picture): InputStream = try {
        storageAdapter.getImage(picture.id)
    } catch (e: StorageAdapter.StorageException) {
        throw ServiceException("Failed to get picture stream", e)
    }

    override suspend fun getFromQueryResult(queryEnumerator: QueryEnumerator): Set<SavedPainting> = getAll(
            queryEnumerator.map { it.key.toString() }.toSet())

    @Synchronized
    override suspend fun delete(painting: SavedPainting) {
        storageAdapter.deleteImage(painting.mainPicture.id)
        painting.wip.forEach { storageAdapter.deleteImage(it.id) }
        painting.references.forEach { storageAdapter.deleteImage(it.id) }
        paintingCRUDAdapter.delete(id = painting.id)
    }

    override suspend fun delete(paintingId: String) {
        paintingCRUDAdapter.delete(id = paintingId)
    }

    /** Get a InputStream of a thumbnail of a picture */
    suspend override fun getPictureThumbnailStream(picture: Picture): InputStream = try {
        storageAdapter.getThumbnail(picture.id)
    } catch (e: StorageAdapter.StorageException) {
        throw ServiceException("Failed to get picture stream", e)
    }

}