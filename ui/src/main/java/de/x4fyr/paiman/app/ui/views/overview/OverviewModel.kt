package de.x4fyr.paiman.app.ui.views.overview

import com.google.gson.Gson
import de.x4fyr.paiman.app.adapter.Base64Encoder
import de.x4fyr.paiman.app.ui.Model
import de.x4fyr.paiman.lib.domain.SavedPainting
import de.x4fyr.paiman.lib.services.PaintingService
import de.x4fyr.paiman.lib.services.QueryService
import kotlinx.coroutines.*
import org.threeten.bp.LocalDate
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*

/** Model in overview MVC */
open class OverviewModel(private val paintingService: PaintingService,
                         queryService: QueryService,
                         val base64Encoder: Base64Encoder) : Model {

    private val gson = Gson()

    val addPaintingModel = AddPaintingModel(base64Encoder = base64Encoder)

    private val liveQuery =
            queryService.allPaintingsQuery.toLiveQuery().also {
                it.addChangeListener {
                    GlobalScope.launch {
                        previews = async {
                            paintingService.getFromQueryResult(it.rows).map {
                                makePreviewFromPainting(it)
                            }.toSet()
                        }
                    }
                }
                it.start()
            }

    /** [Preview] set of all paintings*/
    private var previews: Deferred<Set<Preview>> = GlobalScope.async {
        paintingService.getFromQueryResult(liveQuery.also { it.waitForRows() }.rows).map {
            makePreviewFromPainting(it)
        }.toSet()
    }
        private set

    open fun getPreviews(): String {
        println("Model callback: getPreviews()")
        return runBlocking { gson.toJson(previews.await()) }
    }


    /** Data class containing everything the view need for displaying previews */
    data class Preview(
            /** painting id */
            var id: String,
            /** painting title */
            var title: String,
            /** image in bas64 encoding */
            var base64Image: String)

    /** Holder for "add painting" dialog */
    data class AddPaintingModel(var title: String? = null, var month: Int? = null, var year: Int? = null, var image: ByteArrayInputStream? = null, private val base64Encoder: Base64Encoder) {

        fun setImage(inputStream: InputStream): String {
            val dataString: String
            if (inputStream is ByteArrayInputStream) {
                val tmp = inputStream
                dataString = base64Encoder.jpegDataString(tmp)
                tmp.reset()
                image = tmp
            } else {
                val byteArray = inputStream.readBytes()
                val tmp = ByteArrayInputStream(byteArray)
                dataString = base64Encoder.jpegDataString(tmp)
                tmp.reset()
                image = tmp
            }
            return dataString
        }
    }

    /** Save new painting and return id. null if model is not complete */
    internal suspend fun saveNewPainting(): String? {
        val title = addPaintingModel.title
        val image = addPaintingModel.image
        val month = addPaintingModel.month
        val year = addPaintingModel.year
        return if (title != null && !title.isNullOrBlank() && image != null && month != null && year != null) {
            paintingService.composeNewPainting(title, image, date = LocalDate.of(year, month, 1 )).id
        } else {
            null
        }
    }

    private suspend fun makePreviewFromPainting(painting: SavedPainting): Preview = Preview(id = painting.id,
            title = painting.title,
            base64Image = base64Encoder.jpegDataString(paintingService.getPictureThumbnailStream(painting.mainPicture)))
}