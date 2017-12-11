package de.x4fyr.paiman.lib.service

import javafx.stage.FileChooser
import javafx.stage.Stage

/**
 * PictureProvider implementation for desktop opening the default file selection dialog to let the user pick an image
 */
class JavaFxPictureService(val stage: Stage) {

    /** Pick a picture using [FileChooser] */
    fun pickPicture(onReturn: (url: String?) -> Unit) {
        FileChooser().apply {
            title = "Open picture"
            onReturn(showOpenDialog(stage)?.path)
        }
    }
}