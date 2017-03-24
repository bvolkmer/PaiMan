package de.x4fyr.paiman.app.ui.detail

import de.x4fyr.paiman.app.controller.ServiceController
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import java.io.InputStream

/**
 * Created by x4fyr on 3/23/17.
 */
class Controller : tornadofx.Controller() {
    val view by inject<View>()
    val serviceController by inject<ServiceController>()
    val pictureProvider = serviceController.pictureProvider
    val paintingService = serviceController.paintingService

    fun addWip() {
        pictureProvider.pickPicture(primaryStage) {
            launch(JavaFx) {
                val painting = paintingService.get(view.model.id)
                val inputStream: InputStream? = serviceController.pathToInputStream(it)
                if (inputStream != null) {
                    paintingService.addWipPicture(painting, setOf(inputStream))
                }
            }
        }
    }

    fun addRef() {
        pictureProvider.pickPicture(primaryStage) {
            launch(JavaFx) {
                val painting = paintingService.get(view.model.id)
                val inputStream: InputStream? = serviceController.pathToInputStream(it)
                if (inputStream != null) {
                    paintingService.addReferences(painting, setOf(inputStream))
                }
            }
        }
    }

    fun addTags(tags: List<String>) {
        launch(JavaFx) {
            val painting = paintingService.get(view.model.id)
            paintingService.addTags(painting = painting, tags = tags.toSet())
        }
    }

}