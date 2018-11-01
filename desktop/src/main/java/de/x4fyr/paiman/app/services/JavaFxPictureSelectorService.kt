package de.x4fyr.paiman.app.services

import javafx.stage.FileChooser
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

/**
 * PictureProvider implementation for desktop opening the default file selection dialog to let the user pick an image
 */
class JavaFxPictureSelectorService(private val stage: Stage): PictureSelectorService {

    /** Pick a picture using [FileChooser] */
    override fun pickPicture(onReturn: (InputStream?) -> Unit) {
        GlobalScope.launch(Dispatchers.JavaFx) {
            onReturn(FileChooser().apply {
                title = "Open picture"
                this.extensionFilters.addAll(
                        FileChooser.ExtensionFilter("all", "*.*"),
                        FileChooser.ExtensionFilter("jpeg (*.jpg,*.jpeg)", "*.jpg", "*.jpeg"))
                this.selectedExtensionFilter = this.extensionFilters[1]
            }.showOpenDialog(stage)?.path?.let { Paths.get(it) }?.let { Files.newInputStream(it) })
        }

    }
}