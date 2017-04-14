package de.x4fyr.paiman.app.utils

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.image.ImageView
import javafx.scene.layout.Region
import java.util.logging.Logger


/**
 * Image View Pane allowing advanced resizing
 */
class ImageViewPane @JvmOverloads constructor(imageView: ImageView = ImageView(),
                                              private val hPos: HPos = HPos.CENTER,
                                              private val vPos: VPos = VPos.CENTER
) : Region() {

    private val LOG = Logger.getLogger(this::class.qualifiedName)
    private val imageViewProperty = SimpleObjectProperty<ImageView>()

    private var imageView: ImageView
        get() = imageViewProperty.get()
        set(imageView) = this.imageViewProperty.set(imageView)

    override fun layoutChildren() {
        val imageView = imageView
        imageView.fitWidth = width
        imageView.fitHeight = height
        layoutInArea(imageView, 0.0, 0.0, width, height, 0.0, hPos, vPos)
        super.layoutChildren()
    }

    init {
        imageViewProperty.addListener { _, oldIV, newIV ->
            if (oldIV != null) {
                children.remove(oldIV)
            }
            if (newIV != null) {
                children.add(newIV)
            }
        }
        this.imageViewProperty.set(imageView)
    }

    /** apply delegated to underlying ImageView */
    fun apply(block: ImageView.() -> Unit): ImageViewPane {
        imageView.apply(block)
        return this
    }
}
