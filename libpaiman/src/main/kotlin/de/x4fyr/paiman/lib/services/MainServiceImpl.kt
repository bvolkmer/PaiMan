package de.x4fyr.paiman.lib.services

import com.couchbase.lite.QueryEnumerator
import de.x4fyr.paiman.lib.adapter.database.PaintingCRUDAdapter
import de.x4fyr.paiman.lib.adapter.database.PictureCRUDAdapter
import de.x4fyr.paiman.lib.adapter.database.QueryAdapter
import de.x4fyr.paiman.lib.domain.*
import java.io.InputStream
import org.threeten.bp.LocalDate
import java.util.logging.Logger

/**
 * Main Service implementation combining multiple services
 * @author de.x4fyr
 * Created on 3/1/17.
 */
internal class MainServiceImpl(private var paintingCRUDAdapter: PaintingCRUDAdapter,
                               queryAdapter: QueryAdapter,
                               private val pictureCRUDAdapter: PictureCRUDAdapter) :
        PaintingService,
        QueryService by queryAdapter {

    override fun get(id: String): SavedPainting = paintingCRUDAdapter.read(id) ?: throw ServiceException(
            "Could not get")

    override fun getAll(ids: Set<String>): Set<SavedPainting> = paintingCRUDAdapter.read(ids)

    private var LOG = Logger.getLogger(this::class.simpleName)

    private val dummyPicture = Picture("dummy")

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
        return savedPainting
    }

    override fun replaceMainPicture(painting: SavedPainting,
                                    newPicture: InputStream,
                                    moveOldToWip: Boolean): SavedPainting {
        val mainPictureID = pictureCRUDAdapter.createPicture(newPicture, paintingID = painting.id)
        return paintingCRUDAdapter.update(
                painting = painting.copy(
                        mainPicture = Picture(mainPictureID),
                        wip = if (moveOldToWip) painting.wip + painting.mainPicture else painting.wip))
                ?: throw ServiceException("Could not replace main picture")
    }

    override fun changePainting(painting: SavedPainting): SavedPainting = paintingCRUDAdapter.update(painting)
            ?: throw ServiceException("Could not update painting")

    override fun sellPainting(painting: SavedPainting,
                              purchaser: Purchaser,
                              date: LocalDate,
                              price: Double): SavedPainting = paintingCRUDAdapter.update(painting.copy(
            sellingInfo = SellingInformation(purchaser = purchaser, date = date, price = price)))
            ?: throw ServiceException("Could not sell painting")

    override fun addWipPicture(painting: SavedPainting, images: Set<InputStream>): SavedPainting {
        val wipIDs = images.map { pictureCRUDAdapter.createPicture(pictureStream = it, paintingID = painting.id) }
        return paintingCRUDAdapter.update(painting.copy(wip = painting.wip + wipIDs.map(::Picture)))
                ?: throw ServiceException("Could not add wip")
    }

    override fun removeWipPicture(painting: SavedPainting, images: Set<String>): SavedPainting {
        val savedPainting = paintingCRUDAdapter.update(painting.copy(wip = painting.wip - images.map(::Picture)))
        images.forEach { pictureCRUDAdapter.deletePicture(id = it, paintingID = painting.id) }
        return savedPainting ?: throw ServiceException("Could not remove wip")
    }

    override fun addReferences(painting: SavedPainting,
                               references: Set<InputStream>): SavedPainting {
        val refIDs = references.map { pictureCRUDAdapter.createPicture(pictureStream = it, paintingID = painting.id) }
        return paintingCRUDAdapter.update(painting.copy(references = painting.references + refIDs.map(::Picture)))
                ?: throw ServiceException("Could not add reference")
    }

    override fun removeReferences(painting: SavedPainting, references: Set<String>): SavedPainting {
        val savedPainting = paintingCRUDAdapter.update(
                painting.copy(references = painting.references - references.map(::Picture)))
        references.forEach { pictureCRUDAdapter.deletePicture(id = it, paintingID = painting.id) }
        return savedPainting ?: throw ServiceException("Could not remove painting")
    }

    override fun addTags(painting: SavedPainting, tags: Set<String>): SavedPainting = paintingCRUDAdapter.update(
            painting.copy(tags = painting.tags + tags)) ?: throw ServiceException("Could not add tags")

    override fun removeTags(painting: SavedPainting, tags: Set<String>): SavedPainting = paintingCRUDAdapter.update(
            painting.copy(tags = painting.tags - tags)) ?: throw ServiceException("Could not remove tags")

    override fun getPictureStream(picture: Picture,
                                  painting: SavedPainting): InputStream = pictureCRUDAdapter.readPicture(id =
    picture.id, paintingID = painting.id) ?: throw ServiceException("Could not get picture")

    override fun getAllPictureStreams(pictures: Set<Picture>,
                                      painting: SavedPainting): Set<InputStream> = pictureCRUDAdapter
            .readPictures(pictures.map { it.id }.toSet(), painting.id)

    override fun getFromQueryResult(queryEnumerator: QueryEnumerator): Set<SavedPainting> = getAll(
            queryEnumerator.map { it.key.toString() }.toSet())

    override fun delete(painting: SavedPainting) {
        paintingCRUDAdapter.delete(id = painting.id)
    }

    override fun delete(paintingId: String) {
        paintingCRUDAdapter.delete(id = paintingId)
    }
}