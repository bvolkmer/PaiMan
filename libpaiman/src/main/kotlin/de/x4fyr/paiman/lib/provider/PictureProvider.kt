package de.x4fyr.paiman.lib.provider

import javafx.stage.FileChooser
import javafx.stage.Stage

/**
 * Created by x4fyr on 3/22/17.
 */
interface PictureProvider {
    fun pickPicture(stage: Stage, onReturn: (url: String) -> Unit)
}