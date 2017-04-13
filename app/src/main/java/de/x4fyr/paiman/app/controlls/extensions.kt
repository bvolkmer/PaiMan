package de.x4fyr.paiman.app.controlls

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXSpinner
import com.jfoenix.controls.JFXTextField
import fontAwesomeFx.FontAwesomeIcon
import fontAwesomeFx.FontAwesomeUnicode
import javafx.beans.property.Property
import javafx.event.EventTarget
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.effect.DropShadow
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import tornadofx.*
import java.util.logging.Logger

/**
 * EventTarget extension function adding an imageViewPane
 */
fun EventTarget.imageViewPane(imageView: ImageView? = null,
                              hPos: HPos = HPos.CENTER,
                              vPos: VPos = VPos.CENTER,
                              op: (ImageView.() -> Unit)?): ImageViewPane {
    val addition = if (imageView == null) ImageViewPane(hPos = hPos, vPos = vPos)
    else ImageViewPane(imageView, hPos, vPos)
    this += if (op == null) addition else addition.apply(op)
    return addition
}

/**
 * EventTarget extension function adding a JFXButton
 */
fun EventTarget.jfxButton(text: String? = null, op: (JFXButton.() -> Unit)? = null): JFXButton {
    val addition = if (text == null) JFXButton() else JFXButton(text)
    this += if (op == null) addition else addition.apply(op)
    return addition
}

/**
 * EventTarget extension function adding a JFXButton with a FontAwesome icon
 */
fun EventTarget.jfxButton(icon: FontAwesomeUnicode, op: (JFXButton.() -> Unit)? = null): JFXButton {
    val addition = JFXButton().apply { graphic = FontAwesomeIcon(icon) }
    this += if (op == null) addition else addition.apply(op)
    return addition
}

/**
 * EventTarget extension function adding a JFXSpinner
 */
fun EventTarget.jfxSpinner(op: (JFXSpinner.() -> Unit)? = null): JFXSpinner {
    val addition = JFXSpinner()
    this += if (op == null) addition else addition.apply(op)
    return addition
}

/**
 * EventTarget extension function adding a JFXTextfield
 */
fun EventTarget.jfxTextfield(property: Property<String>, op: (JFXTextField.() -> kotlin.Unit)? = null): JFXTextField {
    val addition = JFXTextField()
    addition.textProperty().bindBidirectional(property)
    this += if (op == null) addition else addition.apply(op)
    return addition
}

/**
 * EventTarget extension function adding a JFXTextField
 */
fun EventTarget.jfxTextfield(op: (JFXTextField.() -> kotlin.Unit)? = null): JFXTextField {
    val addition = JFXTextField()
    this += if (op == null) addition else addition.apply(op)
    return addition
}

/**
 * Elevate by adding a DropShadow depending on z-dimension
 */
fun CssSelectionBlock.elevate(z: Int) = when (z) {
    0 -> this.effect = DropShadow(0.0, 0.0, 0.0, Color.BLACK)
    1 -> this.effect = DropShadow(5.0, 0.0, 2.0, Color.BLACK)
    2 -> this.effect = DropShadow(10.0, 0.0, 2.0, Color.BLACK)
    3 -> this.effect = DropShadow(15.0, 0.0, 2.0, Color.BLACK)
    4 -> this.effect = DropShadow(20.0, 0.0, 2.0, Color.BLACK)
    else -> Logger.getAnonymousLogger().warning(
            "Illegal elevation requested. Expected a value between 0 and 4. Got $z")
}


private fun Any?.isNull() = this == null
private fun Any?.isNotNull() = this != null
/** Boolean Observable of the null state of a value */
fun <T : Any?> Property<T>.isNull() = observable(getter = Property<Any>::getValue::isNull)

/** Boolean Observable of the null state of a value, negated */
fun <T : Any?> Property<T>.isNotNull() = observable(getter = Property<Any>::getValue::isNotNull)
