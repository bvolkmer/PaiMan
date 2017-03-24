package de.x4fyr.paiman.app.ui.main

import de.x4fyr.paiman.app.controller.ServiceController
import de.x4fyr.paiman.app.ui.detail.DetailView
import de.x4fyr.paiman.app.ui.detail.DetailViewModel
import de.x4fyr.paiman.lib.domain.Painting
import de.x4fyr.paiman.lib.domain.SavedPainting
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import tornadofx.*
import java.io.File
import java.io.InputStream
import java.util.logging.Logger

/**
 * Created by x4fyr on 3/18/17.
 */
class Controller : tornadofx.Controller() {
    val view by inject<View>()
    val serviceController by inject<ServiceController>()
    val paintingService = serviceController.paintingService
    val queryService = serviceController.queryService
    val pictureProvider = serviceController.pictureProvider
    val LOG = Logger.getLogger(this::class.simpleName)


    fun initializeController() {
        launch(JavaFx) {
            async(JavaFx) {
                queryService.allPaintingsQuery.toLiveQuery().apply {
                    addChangeListener { event ->
                        launch(JavaFx) {
                            setPaintings(paintingService.getFromQueryResult(event.rows).map { convert(it) })
                        }
                    }
                    start()
                }
            }
            async(JavaFx) {
                setPaintings(paintingService.getFromQueryResult(queryService.allPaintingsQuery.run())
                        .map { convert(it) })
            }
        }
    }

    fun addPainting() {
        view.addOverlay.show()
        view.mainPane.hide()
    }

    fun commitPainting() {
        var success = false
        launch(JavaFx) {
            val mainPictureFile: File = File(view.addInputs.mainPicture.text)
            if (mainPictureFile.exists() && mainPictureFile.canRead()) {
                val inputStream = mainPictureFile.inputStream()
                if (inputStream.available() > 0) {
                    LOG.info("InputStream of size ${inputStream.available()}: ${mainPictureFile.path}")
                    paintingService.composeNewPainting(title = view.addInputs.title.text, mainPicture = inputStream)
                    success = true
                } else {
                    LOG.warning("Trying to read empty file: ${mainPictureFile.path}")
                }
            } else {
                LOG.warning(
                        "Error: ${mainPictureFile.path} -> ${mainPictureFile.exists()} || ${mainPictureFile.canRead()}")
                view.addInputs.mainPictureError.text = "Error"
            }
        }.invokeOnCompletion {
            if (success) {
                view.addOverlay.hide()
                view.mainPane.show()
            }
        }
    }

    fun selectPainting(id: String) {
        with(paintingService.get(id)) {
            val mainPictureStreams: InputStream = paintingService.getPictureStream(picture = mainPicture,
                    painting = this)
            val wipStreams: List<InputStream> = paintingService.getAllPictureStreams(pictures = wip, painting = this)
                    .toList()
            val refStreams: List<InputStream> = paintingService.getAllPictureStreams(pictures = references,
                    painting = this)
                    .toList()
            val viewModel = DetailViewModel(id = id,title = title, mainPicture = mainPictureStreams, wip = wipStreams,
                    references = refStreams, finishingDate = finishingDate, sellingInformation = sellingInfo,
                    tags = tags, finished = finished)
            view.replaceWith(find<DetailView>(mapOf(DetailView::model to viewModel)))
        }
    }

    fun selectMainPicture() {
        pictureProvider.pickPicture(primaryStage) { view.addInputs.mainPicture.text = it }
    }

    private fun convert(painting: SavedPainting): PaintingModel = PaintingModel(id = painting.id,
            mainPicture = paintingService.getPictureStream(painting.mainPicture, painting), title = painting.title)

    private fun setPaintings(paintingModel: List<PaintingModel>) {
        view.paintings.apply {
            clear()
            addAll(paintingModel)
        }
    }
}