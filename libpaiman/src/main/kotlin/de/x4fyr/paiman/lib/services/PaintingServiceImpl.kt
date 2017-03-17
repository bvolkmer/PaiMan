package de.x4fyr.paiman.lib.services

import de.x4fyr.paiman.lib.adapter.DatabaseAdapter
import de.x4fyr.paiman.lib.domain.Painting
import de.x4fyr.paiman.lib.domain.Picture
import de.x4fyr.paiman.lib.domain.Purchaser
import de.x4fyr.paiman.lib.domain.SellingInformation
import java.time.LocalDate

/**
 * @author de.x4fyr
 * Created on 3/1/17.
 */
class PaintingServiceImpl(var databaseAdapter: DatabaseAdapter) : PaintingService {

    override fun composeNewPainting(mainPicture: Picture,
                                    wip: Set<Picture>,
                                    reference: Set<Picture>,
                                    sellingInfo: SellingInformation,
                                    tags: Set<String>): Painting = databaseAdapter.createPainting(Painting(
            mainPicture = mainPicture,
            wip = wip,
            references = reference,
            sellingInfo = sellingInfo,
            tags = tags))

    override fun replaceMainPicture(painting: Painting,
                                    newPicture: Picture,
                                    moveOldToWip: Boolean): Painting = databaseAdapter.updatePainting(
            painting = painting.copy(
                    mainPicture = newPicture,
                    wip = if (moveOldToWip) painting.wip + painting.mainPicture else painting.wip
            ))

    override fun sellPainting(painting: Painting,
                              purchaser: Purchaser,
                              date: LocalDate?,
                              price: Double): Painting = databaseAdapter.updatePainting(painting.copy(
            sellingInfo = SellingInformation(
                    purchaser = purchaser,
                    date = date,
                    price = price)))

    override fun addWipPicture(painting: Painting, images: Set<Picture>): Painting = databaseAdapter.updatePainting(
            painting.copy(wip = painting.wip + images))

    override fun removeWipPicture(painting: Painting, images: Set<Picture>): Painting = databaseAdapter.updatePainting(
            painting.copy(wip = painting.wip - images))

    override fun addReferences(painting: Painting, references: Set<Picture>): Painting = databaseAdapter.updatePainting(
            painting.copy(references = painting.references + references))

    override fun removeReferences(painting: Painting,
                                  references: Set<Picture>): Painting = databaseAdapter.updatePainting(painting.copy(
            references = painting.references - references))

    override fun addTags(painting: Painting,
                         tags: Set<String>): Painting = databaseAdapter.updatePainting(painting.copy(
            tags = painting.tags + tags
    ))

    override fun removeTags(painting: Painting,
                            tags: Set<String>): Painting = databaseAdapter.updatePainting(painting.copy(
            tags =painting.tags - tags
    ))
}