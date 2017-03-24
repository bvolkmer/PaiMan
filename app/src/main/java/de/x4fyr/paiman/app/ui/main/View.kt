package de.x4fyr.paiman.app.ui.main

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXHamburger
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.FlowPane
import javafx.stage.Screen
import tornadofx.*
import java.io.InputStream
import java.util.logging.Logger

/**
 * Created by x4fyr on 3/18/17.
 */
class View : tornadofx.View() {
    val LOG = Logger.getLogger(this::class.simpleName)
    val controller: Controller by inject()
    val bounds = Screen.getPrimary().visualBounds!!
    var mainPane: Node by singleAssign()
    var addOverlay: Form by singleAssign()
    var paintings = mutableListOf<PaintingModel>().observable()

    object AddInputs {
        var title: TextField by singleAssign()
        var mainPicture: TextField by singleAssign()
        var mainPictureError: Label by singleAssign()
    }

    val addInputs: AddInputs = AddInputs

    override val root: Parent = stackpane {
        maxHeight = bounds.height
        maxWidth = bounds.width
        prefHeight = bounds.height
        prefWidth = bounds.width
        mainPane = vbox {
            borderpane {
                useMaxSize = true
                top {
                    gridpane {
                        useMaxWidth = true
                        row {
                            columnConstraints += ColumnConstraints().apply {
                                percentWidth = 50.0
                            }
                            hbox {
                                useMaxWidth = true
                                alignment = Pos.CENTER_LEFT
                                this += JFXHamburger()
                            }
                            hbox {
                                useMaxWidth = true
                                alignment = Pos.CENTER_RIGHT
                                button("+") { setOnAction { controller.addPainting() } }
                            }
                        }
                    }
                }
                center {
                    scrollpane {
                        isFitToWidth = true
                        useMaxSize = true
                        datagrid(paintings) {
                            cellWidth = 150.0
                            cellHeight = 150.0
                            cellCache {
                                onUserSelect(1) {
                                    LOG.info("Selected ${it.title}: ${it.id}")
                                    controller.selectPainting(it.id)
                                }
                                stackpane {
                                    useMaxSize = true
                                    imageview {
                                        isPreserveRatio = true
                                        isSmooth = true
                                        useMaxSize = true
                                        image = Image(it.mainPicture, 150.0, 150.0, true, true)
                                    }
                                    label(it.title)
                                }
                            }
                        }
                    }
                }
            }
        }
        addOverlay = form {
            useMaxSize = true
            hide()
            fieldset("Add painting") {
                field("Title") {
                    addInputs.title = textfield()
                }
                field("Main Picture") {
                    addInputs.mainPicture = textfield()
                    this += JFXButton("+").apply {
                        this.setOnAction {
                            controller.selectMainPicture()
                        }
                    }
                    addInputs.mainPictureError = label()
                }
            }
            button("Commit")
            {
                setOnAction { controller.commitPainting() }
            }
        }
    }

    init {
        controller.initializeController()
    }

}