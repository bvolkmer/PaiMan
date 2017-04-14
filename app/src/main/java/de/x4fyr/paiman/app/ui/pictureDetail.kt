package de.x4fyr.paiman.app.ui

import de.x4fyr.paiman.app.utils.Vector
import de.x4fyr.paiman.app.utils.jfxButton
import fontAwesomeFx.FontAwesomeUnicode.ARROW_LEFT
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import tornadofx.*
import java.util.logging.Logger

/**
 * Fragment used to show details of a single picture
 */

class PictureDetailFragment : de.x4fyr.paiman.app.utils.Fragment() {
    private val LOG = Logger.getLogger(this::class.qualifiedName)
    /** Parameter for the picture to show */
    val picture by param<Image>()
    /** Parameter for the referring UIComponent which will be returned to */
    private val pictureProperty = SimpleObjectProperty<Image>(picture)
    private var moveInProgressProperty = SimpleBooleanProperty(false)
    private var moveInProgress by moveInProgressProperty
    private var touchPointId: Int? = 0
    private var prevPos: Point2D? = null
    private var initialSize = Vector(picture.width, picture.height)
    private var imageView by singleAssign<ImageView>()

    override val root = anchorpane {
        imageView = imageview {
            imageProperty().bind(pictureProperty)
            val zoomFactor = primaryStage.scene.width / initialSize.x
            val eventPos = Vector.ZERO
            zoomCenteringEventPosition(zoomFactor = zoomFactor, eventPosition = eventPos)
        }
        onMousePressed = EventHandler { event ->
            if (!moveInProgress) {
                moveInProgress = true
                prevPos = Point2D(event.sceneX, event.sceneY)
            }
            event.consume()
        }
        onMouseDragged = EventHandler { event ->
            if (moveInProgress) {
                val currPos = Point2D(event.sceneX, event.sceneY)
                val translationVector = Vector(prevPos, currPos)
                imageView.translateX += translationVector.x
                imageView.translateY += translationVector.y
                prevPos = currPos
            }
            event.consume()
        }
        onMouseReleased = EventHandler { event ->
            if (moveInProgress) {
                moveInProgress = false
                prevPos = null
            }
            event.consume()
        }
        onScroll = EventHandler { event ->
            if (System.getProperty("javafx.platform", "desktop") == "desktop")
                imageView.zoomCenteringEventPosition(zoomFactor = if (event.deltaY > 0) 1.2 else 1 / 1.2,
                        eventPosition = Vector(event.sceneX, event.sceneY))
            event.consume()
        }
        onZoom = EventHandler { event ->
            imageView.zoomCenteringEventPosition(zoomFactor = event.zoomFactor,
                    eventPosition = Vector(event.sceneX, event.sceneY))
            event.consume()
        }
        onTouchPressed = EventHandler { event ->
            if (!moveInProgress) {
                moveInProgress = true
                touchPointId = event.touchPoint.id
                prevPos = Point2D(event.touchPoint.sceneX, event.touchPoint.sceneY)
            }
            event.consume()
        }
        onTouchReleased = EventHandler { event ->
            if (event.touchPoint.id == touchPointId) {
                moveInProgress = false
                prevPos = null
            }
        }
        jfxButton(ARROW_LEFT) {
            onAction = EventHandler {
                backToReferrer()
            }
        }
    }

    /** Node extension function zooming a node while centering to the position of an event by translation resp. to the zoom */
    fun Node.zoomCenteringEventPosition(zoomFactor: Double, eventPosition: Vector) {
        val bounds = localToScene(boundsInLocal)
        val imgPos = Vector(bounds.minX, bounds.minY)
        val imgBound = Vector(scaleX, scaleY) * initialSize
        val translation = (eventPosition - (imgPos + imgBound / 2.0)) * (1 - zoomFactor)
        scaleX *= zoomFactor
        scaleY *= zoomFactor
        translateX += translation.x
        translateY += translation.y
    }
}



