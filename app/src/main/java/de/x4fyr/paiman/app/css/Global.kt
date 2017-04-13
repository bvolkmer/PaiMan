package de.x4fyr.paiman.app.css


import de.x4fyr.paiman.app.controlls.elevate
import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Main stylesheet for global rules
 */
class Global : Stylesheet() {

    companion object {
        /** Action bar formatting */
        val actionBar by cssclass()
        private val tabHeaderBackground by cssclass("tab-header-background")
        private val tabSelectedLine by cssclass("tab-selected-line")
        private val jfxButton by cssclass("jfx-button")
        private val textInput by cssclass("text-input")
    }

    /** Unified color deceleration for use across the whole ui */
    @Suppress("unused")
    object COLOR {
        /** Main background color */
        val BACKGROUND = c("#DADADA")
        /** Primary color for highlighted areas and objects */
        val PRIMARY = c("#3F51B5")
        /** Darker tone of PRIMARY */
        val DARK_PRIMARY = c("#303F9F")
        /** Lighter tone of PRIMARY */
        val LIGHT_PRIMARY = c("#C5CAE9")
        /** Text color for use in *PRIMARY colored areas*/
        val TEXT_PRIMARY = c("#FFFFFF")
        /** Accent color */
        val ACCENT = c("#009688")
        /** Main text color */
        val PRIMARY_TEXT = c("#212121")
        /** Less important text color */
        val SECONDARY_TEXT = c("#757575")
        /** Divider color */
        val DIVIDER = c("#BDBDBD")
    }

    /** Rippler main properties */
    object Rippler {
        /** Fill property */
        val fill = COLOR.ACCENT
    }

    init {
        val primary = mixin {
            backgroundColor += COLOR.PRIMARY
            textFill = COLOR.TEXT_PRIMARY
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }
        val normal = mixin {
            fontFamily = "Roboto"
            textFill = COLOR.PRIMARY_TEXT
        }
        val accent = mixin {
            backgroundColor += COLOR.ACCENT
            textFill = COLOR.PRIMARY_TEXT
        }
        root {
            +normal
            backgroundColor += COLOR.BACKGROUND
        }
        actionBar {
            +primary
            prefHeight = 50.px
            elevate(3)
            alignment = Pos.CENTER_LEFT
            val pad = 10.px
            val margin = 10.px
            padding = box(pad, pad, pad + margin, pad)
            borderInsets += box(0.px, 0.px, margin, 0.px)
            backgroundInsets += box(0.px, 0.px, margin, 0.px)
            label {
                +primary
            }
            button {
                +primary
            }
            textInput {
                +primary
                unsafe("-jfx-focus-color", raw("#009688"))
                unsafe("-jfx-unfocus-color", raw(COLOR.LIGHT_PRIMARY.css))
            }
        }
        datagrid {
            backgroundColor += Color.TRANSPARENT
            datagridCell {
                backgroundColor += Color.TRANSPARENT
                +normal
                elevate(1)
                and(selected) {
                    +accent
                }
                and(hover) {
                    elevate(2)
                }
            }
        }
        scrollPane {
            backgroundColor += Color.TRANSPARENT
            viewport {
                backgroundColor += Color.TRANSPARENT
            }
        }
        jfxButton {
            unsafe("-jfx-rippler-fill", COLOR.ACCENT.css)
        }
        label {
            +normal
            padding = box(5.px)
            wrapText = true
        }
        textInput {
            +normal
            padding = box(5.px)
            wrapText = true
        }
        tabHeaderBackground {
            +primary
        }
        tabSelectedLine {
            fill = COLOR.ACCENT
            stroke = COLOR.ACCENT
        }
    }
}

