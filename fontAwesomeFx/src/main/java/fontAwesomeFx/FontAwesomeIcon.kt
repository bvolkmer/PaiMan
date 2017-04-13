package fontAwesomeFx

import javafx.scene.control.Label

/**
 * FontAwesome Icon as extension to Label with right css styles applied
 *
 * @param icon unicode of the icon to represent
 */
class FontAwesomeIcon(icon: FontAwesomeUnicode) : Label(icon.unicode) {
    init {
        styleClass += "fa-icon"
        style = "-fx-font-family: FontAwesome; -fx-background-color: TRANSPARENT;"
    }
}

