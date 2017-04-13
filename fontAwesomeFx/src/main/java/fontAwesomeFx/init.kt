package fontAwesomeFx

import javafx.application.Application
import javafx.scene.text.Font

/**
 * Initialise the FontAwesome font in JavaFx
 *
 * @param app Application to load the font in
 */
fun initialiseFontAwesomeFx(app: Application) {
    Font.loadFont(app::class.java.getResource("/font/FontAwesome.otf").toExternalForm(), 0.0)
}