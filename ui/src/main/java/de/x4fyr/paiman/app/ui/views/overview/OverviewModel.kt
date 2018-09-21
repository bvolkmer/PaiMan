package de.x4fyr.paiman.app.ui.views.overview

import com.google.gson.Gson
import de.x4fyr.paiman.app.ui.Model
import de.x4fyr.paiman.app.ui.jpegDataString
import de.x4fyr.paiman.lib.domain.SavedPainting
import de.x4fyr.paiman.lib.services.PaintingService
import de.x4fyr.paiman.lib.services.QueryService
import kotlinx.coroutines.experimental.*
import java.util.*

/** Model in overview MVC */
open class OverviewModel(private val paintingService: PaintingService,
                    queryService: QueryService): Observable(), Model {

    private val gson = Gson()

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

    private suspend fun makePreviewFromPainting(painting: SavedPainting): Preview = Preview(id = painting.id,
            title = painting.title,
            base64Image = jpegDataString(paintingService .getPictureThumbnailStream(painting.mainPicture)))
}