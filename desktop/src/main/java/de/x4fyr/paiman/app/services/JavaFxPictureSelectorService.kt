package de.x4fyr.paiman.app.services

import de.x4fyr.paiman.app.transform
import javafx.stage.FileChooser
import javafx.stage.Stage
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

/**
 * PictureProvider implementation for desktop opening the default file selection dialog to let the user pick an image
 */
class JavaFxPictureSelectorService(private val stage: Stage): PictureSelectorService {

    /** Pick a picture using [FileChooser] */
    override fun pickPicture(onReturn: (InputStream?) -> Unit) {
        launch(JavaFx) {
            onReturn(FileChooser().apply {
                title = "Open picture"
                this.extensionFilters.addAll(
                        FileChooser.ExtensionFilter("all", "*.*"),
                        FileChooser.ExtensionFilter("jpeg (*.jpg,*.jpeg)", "*.jpg", "*.jpeg"))
                this.selectedExtensionFilter = this.extensionFilters[1]
            }.showOpenDialog(stage)?.path?.transform { Paths.get(it) }?.transform { Files.newInputStream(it) })
        }

    }
}