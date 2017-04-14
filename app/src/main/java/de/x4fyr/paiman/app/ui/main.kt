package de.x4fyr.paiman.app.ui

import com.jfoenix.controls.JFXRippler
import de.x4fyr.paiman.app.controller.ImageCacheController
import de.x4fyr.paiman.app.controller.ServiceController
import de.x4fyr.paiman.app.css.Global
import de.x4fyr.paiman.app.utils.addShortLongPressHandler
import de.x4fyr.paiman.app.utils.jfxButton
import de.x4fyr.paiman.lib.domain.SavedPainting
import fontAwesomeFx.FontAwesomeUnicode
import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.SelectionMode
import javafx.scene.image.Image
import javafx.scene.layout.ColumnConstraints
import javafx.stage.Screen
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import tornadofx.*
import java.util.logging.Logger

/**
 * Controller for the MainView
 */
class MainController : Controller() {
    private val view by inject<MainView>()
    private val serviceController by inject<ServiceController>()
    private val imageCacheController by inject<ImageCacheController>()
    private val paintingService = serviceController.paintingService
    private val queryService = serviceController.queryService


    /** Initialize this controller by loading the pictures */
    fun initializeController(modelProperty: ListProperty<MainModel>) {
        launch(JavaFx) {
            async(JavaFx) {
                queryService.allPaintingsQuery.toLiveQuery().apply {
                    addChangeListener { event ->
                        launch(JavaFx) {
                            setPaintings(paintingService.getFromQueryResult(event.rows).map { convert(it) },
                                    modelProperty)
                        }
                    }
                    start()
                }
            }
            async(JavaFx) {
                reloadModel(modelProperty)
            }
        }
    }

    /** Reload the model from persistence */
    fun reloadModel(modelProperty: ListProperty<MainModel>) {
        launch(JavaFx) {
            setPaintings(paintingService.getFromQueryResult(queryService.allPaintingsQuery.run())
                    .map { convert(it) }, modelProperty)
        }
    }

    /** Open a selected painting */
    fun selectPainting(id: String) {
        view.replaceWith(find<DetailFragment>(mapOf(DetailFragment::paintingId to id)))
    }

    /** Delete given paintings */
    fun delete(paintings: List<MainModel>) {
        launch(JavaFx) {
            paintings.forEach { paintingService.delete(it.id) }
        }
    }

    private fun convert(painting: SavedPainting): MainModel = MainModel(
            id = painting.id,
            mainPicture = imageCacheController.getOrSetThumbnail(painting.mainPicture, painting), title = painting
            .title)

    private fun setPaintings(newModels: List<MainModel>, modelProperty: ListProperty<MainModel>) {
        modelProperty.apply {
            clear()
            setAll(newModels)
        }
    }

}

/**
 * Model containing information used to list paintings
 */
data class MainModel(
        /** id of the painting */
        val id: String,
        /** Main Picture of the painting */
        val mainPicture: Image,
        /** Title of the painting */
        val title: String)


/**
 * Main View of the application
 */
class MainView : tornadofx.View() {
    private val LOG: Logger = Logger.getLogger(this::class.simpleName)
    private val controller: MainController by inject()
    private val bounds = Screen.getPrimary().visualBounds!!
    private var paintings = FXCollections.observableArrayList<MainModel>()
    private var paintingsProperty = SimpleListProperty<MainModel>(paintings)
    private var inSelectionModeProperty: SimpleBooleanProperty = SimpleBooleanProperty(
            false)
    private var inSelectionMode by inSelectionModeProperty
    private var selectedItems by singleAssign<ObservableList<MainModel>>()

    override fun onDock() {
        controller.reloadModel(paintingsProperty)
    }

    override val root = borderpane {
        maxHeight = bounds.height
        maxWidth = bounds.width
        prefHeight = bounds.height
        prefWidth = bounds.width
        useMaxSize = true
        top {
            gridpane {
                addClass(Global.actionBar)
                row {
                    columnConstraints.setAll(
                            ColumnConstraints().apply { percentWidth = 100 / 3.0 },
                            ColumnConstraints().apply { percentWidth = 100 / 3.0 },
                            ColumnConstraints().apply { percentWidth = 100 / 3.0 }
                    )
                    hbox {
                        alignment = Pos.CENTER_LEFT
                        label(messages["appTitle"])
                    }
                    hbox {
                        alignment = Pos.CENTER
                    }
                    stackpane {
                        hbox {
                            alignment = Pos.CENTER_RIGHT
                            visibleProperty().bind(inSelectionModeProperty.not())
                            jfxButton(FontAwesomeUnicode.PLUS) {
                                setOnAction {
                                    replaceWith(
                                            find<CreatePaintingFragment>())
                                }
                            }
                        }
                        hbox {
                            alignment = Pos.CENTER_RIGHT
                            visibleProperty().bind(inSelectionModeProperty)
                            jfxButton(FontAwesomeUnicode.TRASH) {
                                setOnAction {
                                    controller.delete(selectedItems)
                                }
                            }
                        }
                    }
                }
            }
        }
        center {
            datagrid(paintingsProperty) {
                useMaxSize = true
                cellWidth = 150.0
                cellHeight = 150.0
                paintingsProperty.addListener(ListChangeListener {
                    while (it.next()) {
                        inSelectionMode = false
                        selectionModel.clearSelection()
                    }
                })
                selectionModel.selectionMode = SelectionMode.MULTIPLE
                selectedItems = selectionModel.selectedItems
                selectionModel.selectedIndices.addListener(ListChangeListener {
                    while (it.next()) {
                        if (it.wasRemoved() && it.list.size <= 0) inSelectionMode = false
                    }
                })

                cellCache {
                    stackpane {
                        addShortLongPressHandler(consume = true, shortHandler = { _ ->
                            if (inSelectionMode) {
                                val index: Int = this@datagrid.items.indexOf(it)
                                if (selectionModel.isSelected(index)) selectionModel.clearSelection(index)
                                else selectionModel.select(it)
                            } else {
                                LOG.info("Selected ${it.title}: ${it.id}")
                                controller.selectPainting(it.id)
                            }
                        }, longHandler = { _ ->
                            inSelectionMode = true
                            val index: Int = this@datagrid.items.indexOf(it)
                            if (selectionModel.isSelected(index)) selectionModel.clearSelection(index)
                            else selectionModel.select(it)
                        })

                        useMaxSize = true
                        val iv = imageview {
                            isPreserveRatio = true
                            isSmooth = true
                            useMaxSize = true
                            image = it.mainPicture
                        }
                        this.addChildIfPossible(JFXRippler(iv).apply {
                            ripplerFill = Global.Rippler.fill
                        })
                    }
                }
            }
        }
    }

    init {
        controller.initializeController(paintingsProperty)
    }

}