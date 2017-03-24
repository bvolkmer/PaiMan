package de.x4fyr.paiman.app.ui.detail

import com.jfoenix.controls.JFXButton
import de.x4fyr.paiman.app.ui.main.MainView
import de.x4fyr.paiman.app.ui.sell.SellViewModel
import de.x4fyr.paiman.app.ui.sell.SellView
import de.x4fyr.paiman.lib.domain.SellingInformation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.input.TouchEvent
import javafx.stage.Screen
import tornadofx.*

/**
 * Created by x4fyr on 3/23/17.
 */
class View : tornadofx.View() {
    val controller by inject<Controller>()
    val model by param<Model>()
    val bounds = Screen.getPrimary().visualBounds!!
    var tagInput by singleAssign<Node>()

    override val root = vbox {
        maxHeight = bounds.height
        maxWidth = bounds.width
        prefHeight = bounds.height
        prefWidth = bounds.width
        alignment = Pos.TOP_LEFT
        this += button("<-") { setOnAction { replaceWith(find<MainView>()) } }
        scrollpane {
            fitToWidthProperty().set(false)
            addEventHandler(TouchEvent.TOUCH_MOVED) { it.consume() }
            prefViewportWidthProperty().bind(primaryStage.widthProperty())
            maxWidthProperty().bind(primaryStage.widthProperty())
            with(model) {
                vbox {
                    stackpane {
                        alignment = Pos.TOP_CENTER
                        maxWidthProperty().bind(primaryStage.widthProperty())
                        imageview {
                            fitWidthProperty().bind(primaryStage.widthProperty())
                            isSmooth = true
                            isPreserveRatio = true
                            image = Image(mainPicture)
                        }
                        label(title)
                    }
                    vbox {
                        hbox {
                            label(tags.joinToString(separator = ", ", prefix = "", postfix = ""))
                            this += JFXButton("+").apply { setOnAction { tagInput.show() } }
                        }
                        tagInput = hbox {
                            hide()
                            val textInput = textfield {}
                            this += JFXButton("+").apply {
                                setOnAction {
                                    controller.addTags(listOf(textInput.text))
                                    tagInput.hide()
                                }
                            }
                        }
                    }
                    if (finished) {
                        label("Finished at ${finishingDate}")
                    }
                    with(sellingInformation) {
                        if (this != null) {
                            label("Sold on $date at a price of $price to ${purchaser.name} at ${purchaser.address}")
                        } else {
                            this@vbox += JFXButton("Sell").apply {
                                setOnAction {
                                    val viewModel = SellViewModel(SellingInformation())
                                    replaceWith(find<SellView>(mapOf(
                                            SellView::model to viewModel,
                                            SellView::referrer to this@View,
                                            SellView::paintingId to this@View.model.id))
                                    )
                                }
                            }
                        }
                    }
                    vbox {
                        hbox {
                            label("WIPs")
                            this += JFXButton("+").apply { setOnAction { controller.addWip() } }
                        }
                        datagrid(wip) {
                            cellHeight = 150.0
                            cellWidth = 150.0
                            minHeight = 0.0
                            cellCache {
                                imageview {
                                    useMaxSize = true
                                    isSmooth = true
                                    isPreserveRatio = true
                                    image = Image(it, 150.0, 150.0, true, true)
                                }
                            }
                        }
                    }
                    vbox {
                        hbox {
                            label("References")
                            this += JFXButton("+").apply { setOnAction { controller.addRef() } }
                        }
                        datagrid(references) {
                            cellHeight = 150.0
                            cellWidth = 150.0
                            minHeight = 0.0
                            cellCache {
                                imageview {
                                    useMaxSize = true
                                    isSmooth = true
                                    isPreserveRatio = true
                                    image = Image(it, 150.0, 150.0, true, true)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}