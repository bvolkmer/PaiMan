package de.x4fyr.paiman.app.ui.views.overview

import com.google.gson.Gson
import de.x4fyr.paiman.app.ui.Model
import de.x4fyr.paiman.app.ui.jpegDataString
import de.x4fyr.paiman.lib.domain.SavedPainting
import de.x4fyr.paiman.lib.services.PaintingService
import de.x4fyr.paiman.lib.services.QueryService
import kotlinx.coroutines.experimental.*
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*

/** Model in overview MVC */
open class OverviewModel(private val paintingService: PaintingService,
                         queryService: QueryService) : Observable(), Model {

    private val gson = Gson()

    val addPaintingModel = AddPaintingModel();

    private val liveQuery =
            queryService.allPaintingsQuery.toLiveQuery().also {
                it.addChangeListener {
                    launch(CommonPool) {
                        previews = async {
                            paintingService.getFromQueryResult(it.rows).map {
                                makePreviewFromPainting(it)
                            }.toSet().also {
                                setChanged()
                                notifyObservers()
                            }
                        }
                    }
                }
                it.start()
            }

    /** [Preview] set of all paintings*/
    var previews: Deferred<Set<Preview>> = async(CommonPool) {
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
    data class AddPaintingModel(var title: String? = null, var image: ByteArrayInputStream? = null) {

        fun setImage(inputStream: InputStream): String {
            val dataString: String
            if (inputStream is ByteArrayInputStream) {
                val tmp = inputStream
                dataString = jpegDataString(tmp)
                tmp.reset()
                image = tmp
            } else {
                val byteArray = inputStream.readBytes()
                val tmp = ByteArrayInputStream(byteArray)
                dataString = jpegDataString(tmp)
                tmp.reset()
                image = tmp
            }
            return dataString
        }
    }

    /** Save new painting and return id. null if model is not complete */
    suspend fun saveNewPainting(): String? {
        val title = addPaintingModel.title
        val image = addPaintingModel.image
        return if (title != null && !title.isNullOrBlank() && image != null) {
            paintingService.composeNewPainting(title, image).id
        } else {
            null
        }
    }

    private suspend fun makePreviewFromPainting(painting: SavedPainting): Preview = Preview(id = painting.id,
            title = painting.title,
            base64Image = jpegDataString(paintingService.getPictureThumbnailStream(painting.mainPicture)))
}