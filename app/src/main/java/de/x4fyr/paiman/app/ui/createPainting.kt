package de.x4fyr.paiman.app.ui

import de.x4fyr.paiman.app.controller.ServiceController
import de.x4fyr.paiman.app.controlls.*
import de.x4fyr.paiman.app.css.Global
import fontAwesomeFx.FontAwesomeUnicode
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.layout.ColumnConstraints
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import tornadofx.*
import java.io.File
import java.util.logging.Logger

/**
 * Controller for a painting creation dialog
 */
class CreatePaintingController : Controller() {
    private val serviceController by inject<ServiceController>()
    private val pictureProvider = serviceController.pictureProvider
    private val paintingService = serviceController.paintingService
    private val LOG = Logger.getLogger(this::class.qualifiedName)
    private val model by inject<CreatePaintingModel>()

    /** pick image action */
    fun pickImage() {
        pictureProvider.pickPicture {
            if (it != null) {
                try {
                    model.mainPictureImage.value = Image("file://$it")
                    model.mainPictureUrl.value = it
                } catch (e: Exception) {
                    LOG.warning("Couldn't load file $it to an image: ${e.message}")
                }
            }
        }
    }

    /** save action */
    fun save(fragment: CreatePaintingFragment) {
        model.commit()
        val modelItem = model.item
        launch(JavaFx) {
            var success = false
            val mainPictureFile: File = File(modelItem.mainPictureUrl)
            if (mainPictureFile.exists() && mainPictureFile.canRead()) {
                val inputStream = mainPictureFile.inputStream()
                if (inputStream.available() > 0) {
                    LOG.info("InputStream of size ${inputStream.available()}: ${mainPictureFile.path}")
                    paintingService.composeNewPainting(title = modelItem.title, mainPicture = inputStream)
                    success = true
                } else {
                    LOG.warning("Trying to read empty file: ${mainPictureFile.path}")
                }
            } else {
                LOG.warning(
                        "Error: ${mainPictureFile.path} -> ${mainPictureFile.exists()} || ${mainPictureFile.canRead()}")
            }
            if (success) {
                fragment.replaceWith(find<MainView>()) //TODO: Redirect to detail view
            } else {
                LOG.warning("Saving finished unsuccessful")
            }
        }
    }
}

/**
 * Model for creating a new painting
 */
class CreatePaintingModel : ItemViewModel<CreatePaintingModel.Holder>() {
    /** title of the painting to be created */
    val title = bind { item?.titleProperty }
    /** Image containing the mainPicture */
    val mainPictureImage = bind { item?.mainPictureImageProperty }
    /** Url of the mainPicture */
    val mainPictureUrl = bind { item?.mainPictureUrlProperty }

    /** Holder class to hold the creation information */
    @Suppress("KDocMissingDocumentation")
    class Holder {
        val titleProperty = SimpleStringProperty()
        var title: String by titleProperty

        val mainPictureUrlProperty = SimpleStringProperty()
        var mainPictureUrl: String by mainPictureUrlProperty

        val mainPictureImageProperty = SimpleObjectProperty<Image>()
        var mainPictureImage: Image by mainPictureImageProperty
    }
}

/**
 * Fragment for creating paintings
 */
class CreatePaintingFragment : Fragment() {
    private val model by inject<CreatePaintingModel>()
    private val controller by inject<CreatePaintingController>()
    private val LOG = Logger.getLogger(this::class.qualifiedName)

    init {
        model.itemProperty.set(CreatePaintingModel.Holder())
        model.item.title = ""
    }

    override fun onDock() {
        super.onDock()
        model.itemProperty.set(CreatePaintingModel.Holder())
        model.item.title = ""
    }

    override val root = borderpane {
        top {
            gridpane {
                addClass(Global.actionBar)
                useMaxWidth = true
                row {
                    columnConstraints.setAll(
                            ColumnConstraints().apply { percentWidth = 100 / 3.0 },
                            ColumnConstraints().apply { percentWidth = 100 / 3.0 },
                            ColumnConstraints().apply { percentWidth = 100 / 3.0 }
                    )
                    hbox {
                        alignment = Pos.CENTER_LEFT
                        jfxButton(FontAwesomeUnicode.REMOVE) {
                            setOnAction { replaceWith(find<MainView>()) }
                        }
                    }
                    hbox {
                        alignment = Pos.CENTER
                        label(messages["formTitle"])
                    }
                    hbox {
                        alignment = Pos.CENTER_RIGHT
                        jfxButton(FontAwesomeUnicode.OK) {
                            isVisible = false
                            visibleProperty().bind(model.valid)
                            setOnAction { controller.save(this@CreatePaintingFragment) }
                        }
                    }
                }
            }
        }
        center {
            vbox {
                val titleInput = jfxTextfield(model.title) {
                    promptText = messages["title"]
                    model.validationContext.decorationProvider = { StubDecorator() }

                }
                jfxButton(FontAwesomeUnicode.PICTURE) {
                    visibleProperty().bind(model.mainPictureImage.isNull())
                    setOnAction { controller.pickImage() }
                }
                val imageView = imageViewPane {
                    isPreserveRatio = true
                    visibleProperty().bind(model.mainPictureImage.isNotNull())
                    imageProperty().bind(model.mainPictureImage)
                    setOnMouseClicked { controller.pickImage() }
                }
                model.validationContext.addValidator(titleInput) {
                    if (it.isNullOrBlank()) error("Title required") else null
                }
                model.validationContext.addValidator(node = imageView, property = model.mainPictureImage) {
                    if (it == null) error("Image required") else null
                }
            }
        }
    }

}