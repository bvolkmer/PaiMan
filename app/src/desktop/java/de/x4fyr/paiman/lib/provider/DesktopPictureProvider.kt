package de.x4fyr.paiman.lib.provider

import javafx.stage.FileChooser
import javafx.stage.Stage

/**
 * PictureProvider implementation for desktop opening the default file selection dialog to let the user pick an image
 */
class DesktopPictureProvider: PictureProvider {
    private var stage: Stage = tornadofx.DefaultScope.workspace.primaryStage

    override fun pickPicture(onReturn: (url: String?) -> Unit) {
        FileChooser().apply {
            title = "Open picture"
            onReturn(showOpenDialog(stage)?.path)
        }
    }
}