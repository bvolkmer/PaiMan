package de.x4fyr.paiman.app.ui.views.paintingDetail

import de.x4fyr.paiman.app.ui.jpegDataString
import de.x4fyr.paiman.lib.domain.Painting
import de.x4fyr.paiman.lib.domain.SavedPainting
import de.x4fyr.paiman.lib.services.PaintingService
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.threeten.bp.LocalDate
import java.io.InputStream
import kotlin.coroutines.experimental.buildSequence

class PaintingDetailModel(private val paintingService: PaintingService, private val id: String) {

    var view: PaintingDetailView? = null

    var painting: Deferred<SavedPainting> = async {
        paintingService.get(id)
    }

    val mainPicture: Deferred<String> = async {
        jpegDataString(paintingService.getPictureStream(painting.await().mainPicture))
    }

    val wips: Deferred<List<String>>
        get() = async {
            painting.await().wip.map {
                jpegDataString(paintingService.getPictureThumbnailStream(it))
            }
        }

    val refs: Deferred<List<String>>
        get() = async {
            painting.await().references.map {
                jpegDataString(paintingService.getPictureThumbnailStream(it))
            }
        }

    suspend fun addWip(stream: InputStream) {
        val resultPainting = paintingService.addWipPicture(painting.await(), setOf(stream))
        painting = async { resultPainting }
        view?.update(PaintingDetailView.ID.WIPS)
    }

    suspend fun addRef(stream: InputStream) {
        val resultPainting = paintingService.addReferences(painting.await(), setOf(stream))
        painting = async { resultPainting }
        view?.update(PaintingDetailView.ID.REFS)
    }

    suspend fun addTag(tag: String) {
        val resultPainting = paintingService.addTags(painting.await(), setOf(tag))
        painting = async { resultPainting }
        view?.update(PaintingDetailView.ID.TAGS)
    }

    suspend fun finishing(date: LocalDate) {
        val resultPainting: SavedPainting = paintingService.changePainting(painting.await().copy(finished = true, finishingDate = date))
        painting = async { resultPainting }
        view?.update(PaintingDetailView.ID.FINISH)
    }
}