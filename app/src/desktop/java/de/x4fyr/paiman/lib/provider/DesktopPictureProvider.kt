package de.x4fyr.paiman.lib.provider

import de.x4fyr.paiman.lib.provider.PictureProvider
import javafx.stage.FileChooser
import javafx.stage.Stage

/**
 * Created by x4fyr on 3/24/17.
 */
class DesktopPictureProvider: PictureProvider {
    override fun pickPicture(stage: Stage, onReturn: (url: String) -> Unit) {
        FileChooser().apply {
            title = "Open picture"
            onReturn(showOpenDialog(stage).path)
        }
    }
}