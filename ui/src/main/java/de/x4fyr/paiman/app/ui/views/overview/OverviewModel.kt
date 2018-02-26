package de.x4fyr.paiman.app.ui.views.overview

import de.x4fyr.paiman.lib.domain.SavedPainting
import de.x4fyr.paiman.lib.services.PaintingService
import de.x4fyr.paiman.lib.services.QueryService
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.util.Base64
import java.util.Observable

/** Model in overview MVC */
class OverviewModel(private val paintingService: PaintingService,
                    queryService: QueryService): Observable() {

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

    /** Data class containing everything the view need for displaying previews */
    data class Preview(
            /** painting id */
            var id: String,
            /** painting title */
            var title: String,
            /** image in bas64 encoding */
            var bas64Image: String)

    private suspend fun makePreviewFromPainting(painting: SavedPainting): Preview = Preview(id = painting.id,
            title = painting.title,
            bas64Image = Base64.getEncoder()
                    .encodeToString( paintingService
                            .getPictureThumbnailStream(painting.mainPicture)
                            .readBytes()))
}