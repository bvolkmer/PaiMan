package de.x4fyr.paiman.app.controller

import de.x4fyr.paiman.lib.domain.Picture
import de.x4fyr.paiman.lib.domain.SavedPainting
import javafx.scene.image.Image
import tornadofx.*

/** Default preview size */
const val PREVIEW_SIZE = 150.0
/** Default smooth resize boolean */
const val IS_SMOOTH = true

/**
 * Controller for Image cache actions and maintaining
 */
class ImageCacheController : Controller() {
    private val serviceController by inject<ServiceController>()
    private val paintingService = serviceController.paintingService

    private val fullSize: MutableMap<String, Image> = mutableMapOf()
    private val thumbnail: MutableMap<String, Image> = mutableMapOf()

    /**
     * Get or set a full size image of a picture
     *
     * @param picture picture to get or set as an image
     * @param painting painting the picture belongs to
     */
    fun getOrSetFullSize(picture: Picture, painting: SavedPainting): Image {
        var img = fullSize[picture.id]
        if (img == null) {
            val stream = paintingService.getPictureStream(picture, painting)
            img = Image(stream)
            fullSize += picture.id to img
        }
        return img
    }

    /**
     * Get or set a thumbnail of a picture
     *
     * @param picture picture to get or set as an image
     * @param painting painting the picture belongs to
     */
    fun getOrSetThumbnail(picture: Picture, painting: SavedPainting): Image {
        var img = thumbnail[picture.id]
        if (img == null) {
            val stream = paintingService.getPictureStream(picture, painting)
            img = Image(stream, PREVIEW_SIZE, PREVIEW_SIZE, true, true)
            thumbnail += picture.id to img
        }
        return img
    }

}