package de.x4fyr.paiman.app.ui

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDatePicker
import com.jfoenix.controls.JFXTabPane
import de.x4fyr.paiman.app.controller.IS_SMOOTH
import de.x4fyr.paiman.app.controller.ImageCacheController
import de.x4fyr.paiman.app.controller.PREVIEW_SIZE
import de.x4fyr.paiman.app.controller.ServiceController
import de.x4fyr.paiman.app.css.Global
import de.x4fyr.paiman.app.utils.jfxButton
import de.x4fyr.paiman.app.utils.jfxTextfield
import de.x4fyr.paiman.lib.domain.Purchaser
import de.x4fyr.paiman.lib.domain.SellingInformation
import fontAwesomeFx.FontAwesomeIcon
import fontAwesomeFx.FontAwesomeUnicode.*
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.ScrollBar
import javafx.scene.control.Tab
import javafx.scene.control.TextFormatter
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.util.converter.NumberStringConverter
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import tornadofx.*
import java.io.InputStream
import java.text.MessageFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.function.Supplier
import java.util.logging.Logger

/**
 * Controller for painting detail fragment
 */
class DetailController : tornadofx.Controller() {
    private val LOG = Logger.getLogger(this::class.qualifiedName)
    private val serviceController by inject<ServiceController>()
    private val imageCacheController by inject<ImageCacheController>()
    private val pictureProvider = serviceController.pictureProvider
    private val paintingService = serviceController.paintingService

    /** Load the model from persistence */
    fun loadModel(model: PaintingModel, paintingId: String) {
        val painting = paintingService.get(paintingId)
        with(model) {
            id = painting.id
            title = painting.title
            mainPicture = imageCacheController.getOrSetFullSize(painting.mainPicture, painting)
            launch(JavaFx) {
                wip.clear()
                painting.wip.forEach {
                    async(JavaFx) {
                        wip.add(imageCacheController.getOrSetThumbnail(it, painting))
                    }
                }
                references.clear()
                painting.references.forEach {
                    async(JavaFx) {
                        references.add(imageCacheController.getOrSetThumbnail(it, painting))
                    }
                }
            }
            tags.clear()
            tags.setAll(painting.tags)
            setSellingInformationOf(painting.sellingInfo)
            finishingDate = painting.finishingDate
            finished = painting.finished
        }
    }

    internal fun setFinishingDate(model: PaintingModel) {
        launch(JavaFx) {
            val painting = paintingService.get(model.id)
            paintingService.changePainting(painting.copy(finished = model.finishingDate != null, finishingDate = model.finishingDate))
            loadModel(model, painting.id)
        }
    }

    internal fun changeMainImage(model: PaintingModel) {
        pictureProvider.pickPicture {
            if (it != null) {
                launch(JavaFx) {
                    val painting = paintingService.get(model.id)
                    val inputStream = serviceController.pathToInputStream(it)
                    if (inputStream != null) paintingService.replaceMainPicture(painting = painting, newPicture = inputStream, moveOldToWip = false)
                    loadModel(model, paintingId = painting.id)
                }
            }
        }
    }

    internal fun addWip(model: PaintingModel) {
        pictureProvider.pickPicture {
            if (it != null) {
                launch(JavaFx) {
                    val painting = paintingService.get(model.id)
                    val inputStream: InputStream? = serviceController.pathToInputStream(it)
                    if (inputStream != null) {
                        paintingService.addWipPicture(painting, setOf(inputStream))
                    }
                    loadModel(model, painting.id)
                }
            }
        }
    }

    internal fun addRef(model: PaintingModel) {
        pictureProvider.pickPicture {
            if (it != null) {
                launch(JavaFx) {
                    val painting = paintingService.get(model.id)
                    val inputStream: InputStream? = serviceController.pathToInputStream(it)
                    if (inputStream != null) {
                        paintingService.addReferences(painting, setOf(inputStream))
                    }
                    loadModel(model, painting.id)
                }
            }
        }
    }

    internal fun setTags(tags: List<String>, model: PaintingModel) {
        launch(JavaFx) {
            val painting = paintingService.get(model.id)
            paintingService.changePainting(painting.copy(tags = tags.toSet()))
            loadModel(model, painting.id)
        }
    }

    internal fun commitSellingInformation(model: PaintingModel) {
        launch(JavaFx) {
            val painting = paintingService.get(model.id)
            with(model.sellingInformation) {
                LOG.info("SellInfo: ${purchaser.name}, ${purchaser.address}, $price, $date")
                paintingService.sellPainting(painting = painting, date = date, price = price,
                        purchaser = Purchaser(name = purchaser.name, address = purchaser.address))
            }
            loadModel(model, painting.id)
        }
    }

    internal fun changeTitle(model: PaintingModel) {
        launch(JavaFx) {
            val painting = paintingService.get(model.id)
            paintingService.changePainting(painting.copy(title = model.title))
            loadModel(model, painting.id)
        }
    }
}

/**
 * View showing the details of a painting
 */
class DetailFragment : de.x4fyr.paiman.app.utils.Fragment() {
    private val LOG = Logger.getLogger(this::class.qualifiedName)
    private val serviceController by inject<ServiceController>()
    /** ID parameter for the painting to show*/
    val paintingId by param<String>()
    private val controller by inject<DetailController>()
    private val model = PaintingModel()
    private var sellInput by singleAssign<Node>()
    private var imagePane by singleAssign<AnchorPane>()
    private var scrollBarWidth by singleAssign<Double>()
    private var widthChangeListener: ((Any, Any, Number) -> Unit) = { _, _, new ->
        val scaleFactor = ((new as Double) - scrollBarWidth) / model.mainPicture.width
        imagePane.prefHeight = if (scaleFactor < 1) scaleFactor * model.mainPicture.height else model.mainPicture.height
    }

    init {
        controller.loadModel(model, paintingId)
    }

    /**  */
    override fun onDock() {
        LOG.info("onDock: $paintingId")
        controller.loadModel(model, paintingId)
        serviceController.platformService.stopProfiling()
    }

    /**  */
    override fun onUndock() {
        primaryStage.scene.widthProperty().removeListener(widthChangeListener)
    }

    /**  */
    override val root = stackpane {
        Supplier { }
        alignment = Pos.TOP_LEFT
        scrollpane {
            isFitToWidth = true
            useMaxWidth = true
            scrollBarWidth = if (System.getProperty("javafx.platform", "desktop") == "desktop") ScrollBar().width
            else 0.0
            with(model) {
                vbox {
                    useMaxWidth = true
                    imagePane = anchorpane {
                        val scaleFactor = (primaryStage.scene.width - scrollBarWidth) / model.mainPicture.width
                        prefHeight = if (scaleFactor < 1) scaleFactor * model.mainPicture.height else model.mainPicture.height
                        primaryStage.scene.widthProperty().addListener(widthChangeListener)
                        alignment = Pos.BOTTOM_CENTER
                        background = Background(BackgroundImage(mainPicture,
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundPosition(Side.LEFT, 0.0, false, Side.TOP, 0.0, false),
                                BackgroundSize(mainPicture.width, BackgroundSize.AUTO, false, false, true, false)))
                        mainPictureProperty.addListener({ _, oldValue, newValue ->
                            if (newValue != oldValue) {
                                background = Background(BackgroundImage(newValue,
                                        BackgroundRepeat.NO_REPEAT,
                                        BackgroundRepeat.NO_REPEAT,
                                        BackgroundPosition(Side.LEFT, 0.0, false, Side.TOP, 0.0, false),
                                        BackgroundSize(mainPicture.width, BackgroundSize.AUTO, false, false, true, false)))
                            }
                        })
                        onDoubleClick {
                            this@DetailFragment.replaceWith(find<PictureDetailFragment>(mapOf(
                                    PictureDetailFragment::referrer to this@DetailFragment,
                                    PictureDetailFragment::picture to model.mainPicture
                            )))
                        }
                        jfxButton(PENCIL) {
                            anchorpaneConstraints {
                                rightAnchor = 10.0
                                topAnchor = 10.0
                                onAction = EventHandler {
                                    controller.changeMainImage(model)
                                }
                            }
                        }
                    }
                    hbox {
                        addClass(Global.actionBar)
                        anchorpaneConstraints {
                            bottomAnchor = 0.0
                            leftAnchor = 0.0
                        }
                        isFillWidth = true
                        val label = label(titleProperty)
                        val input = jfxTextfield(titleProperty) {
                            hide()
                            focusColor = Global.COLOR.ACCENT
                            unFocusColor = Global.COLOR.LIGHT_PRIMARY
                        }
                        label.onMouseClicked = EventHandler {
                            label.hide()
                            input.show()
                            input.requestFocus()
                        }
                        input.onAction = EventHandler {
                            controller.changeTitle(model)
                            label.show()
                            input.hide()
                        }
                    }
                    vbox boundingBox@ {
                        label(messages["tagsTitle"])
                        val tagStringProperty = SimpleStringProperty(
                                tags.joinToString(separator = " ", prefix = "", postfix = ""))
                        tags.addListener(ListChangeListener {
                            while (it.next()) {
                                tagStringProperty.set(tags.joinToString(separator = " ", prefix = "", postfix = ""))
                            }
                        })
                        val label = label(tagStringProperty) {
                            isVisible = tags.toList().filterNotNull().filter(String::isNotBlank).isNotEmpty()
                        }
                        val input = jfxTextfield(tagStringProperty) {
                            prefWidth = this@boundingBox.width
                            this@boundingBox.boundsInLocalProperty().addListener({ _, oldValue, newValue ->
                                if (newValue.width != oldValue.width) {
                                    this.prefWidth = newValue.width
                                }
                            })
                            isVisible = tags.size <= 0
                            tags.addListener(ListChangeListener {
                                while (it.next()) {
                                    isVisible = tags.toList().filterNotNull().filter(String::isNotBlank).isEmpty()
                                }
                            })
                            focusColor = Global.COLOR.ACCENT
                            unFocusColor = Global.COLOR.LIGHT_PRIMARY
                            promptText = messages["tagsInput"]
                        }
                        label.onMouseClicked = EventHandler {
                            label.hide()
                            input.show()
                            input.requestFocus()
                        }
                        input.onAction = EventHandler {
                            controller.setTags(input.text.split(delimiters = ' '), model)
                            input.hide()
                            label.show()
                        }
                    }
                    hbox {
                        label(messages["finished"])
                        FontAwesomeIcon(REMOVE_SIGN).apply {
                            visibleProperty().bind(finishedProperty.not())
                        }
                        this += JFXDatePicker().apply {
                            bind(finishingDateProperty)
                            promptText = messages["finishingDate"]
                            onAction = EventHandler {
                                controller.setFinishingDate(model)
                            }
                        }
                    }
                    vbox {
                        var showButton by singleAssign<JFXButton>()
                        hbox {
                            label(messages["sellingTitle"])
                            showButton = jfxButton(PENCIL).apply {
                                setOnAction {
                                    sellInput.show()
                                    this.hide()
                                }
                            }
                        }
                        with(sellingInformation) {
                            label {
                                visibleProperty().bind(soldProperty)
                                sellingInformationProperty.addListener { _, _, new ->
                                    with(new) {
                                        text = MessageFormat(messages["sellerInfoText"]).format(arrayOf(date
                                                .format(DateTimeFormatter.ISO_LOCAL_DATE),
                                                price,
                                                purchaser.name, purchaser.address))
                                    }
                                }
                            }
                            label(messages["notSold"]) {
                                visibleProperty().bind(soldProperty.not())
                            }
                            sellInput = vbox {
                                hide()
                                form {
                                    val viewModel = SellingInformationViewModel().apply {
                                        item = model.sellingInformation
                                    }
                                    with(viewModel) {
                                        jfxTextfield(name) {
                                            promptText = messages["sellerNameField"]
                                        }
                                        jfxTextfield(address) {
                                            promptText = messages["sellerAddressField"]
                                        }
                                        jfxTextfield {
                                            textProperty().bindBidirectional(price, NumberStringConverter())
                                            promptText = messages["sellPriceField"]
                                            textFormatter = TextFormatter(NumberStringConverter())
                                        }
                                        this@form += JFXDatePicker().apply {
                                            bind(dateProperty)
                                            promptText = messages["sellDate"]
                                        }
                                        hbox {
                                            jfxButton(REMOVE).apply {
                                                setOnAction {
                                                    rollback()
                                                    showButton.show()
                                                    sellInput.hide()
                                                }
                                            }
                                            jfxButton(OK).apply {
                                                setOnAction {
                                                    commit { model.sellingInformationProperty.set(item) }
                                                    LOG.info("${model.sellingInformation.purchaser.address} + " +
                                                            "${viewModel.address.value} -> ${viewModel.item.purchaser.address}")
                                                    controller.commitSellingInformation(model)
                                                    showButton.show()
                                                    sellInput.hide()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    anchorpane {
                        this += JFXTabPane().apply {
                            anchorpaneConstraints {
                                topAnchor = 0.0
                                bottomAnchor = 0.0
                                leftAnchor = 0.0
                                rightAnchor = 0.0
                            }
                            tabs.addAll(Tab().apply {
                                text = messages["wipTabTitle"]
                                content = hbox {
                                    jfxButton(PLUS).apply { setOnAction { controller.addWip(model) } }
                                    datagrid(wip) {
                                        maxRows = 1
                                        useMaxWidth = true
                                        cellHeight = PREVIEW_SIZE
                                        cellWidth = PREVIEW_SIZE
                                        minHeight = 0.0
                                        cellCache {
                                            imageview {
                                                useMaxSize = true
                                                isSmooth = IS_SMOOTH
                                                isPreserveRatio = true
                                                image = it
                                            }
                                        }
                                    }
                                }
                            }, Tab().apply {
                                text = messages["refTabTitle"]
                                content = hbox {
                                    jfxButton(PLUS).apply { setOnAction { controller.addRef(model) } }
                                    datagrid(references) {
                                        maxRows = 1
                                        useMaxWidth = true
                                        cellHeight = PREVIEW_SIZE
                                        cellWidth = PREVIEW_SIZE
                                        minHeight = 0.0
                                        cellCache {
                                            imageview {
                                                useMaxSize = true
                                                isSmooth = IS_SMOOTH
                                                isPreserveRatio = true
                                                image = it
                                            }
                                        }
                                    }
                                }
                            })
                        }
                        jfxButton(PLUS) {
                            anchorpaneConstraints {
                                topAnchor = 0.0
                                rightAnchor = 0.0
                            }

                        }
                    }
                }
            }
        }
        jfxButton(ARROW_LEFT) { setOnAction { backToReferrer() } }
    }
}

/**
 * Model representing a painitng in detail fragment
 */
@Suppress("KDocMissingDocumentation", "unused")
class PaintingModel {
    val idProperty = SimpleStringProperty()
    var id: String by idProperty
    val titleProperty = SimpleStringProperty()
    var title: String by titleProperty
    val mainPictureProperty = SimpleObjectProperty<Image>()
    var mainPicture: Image by mainPictureProperty
    var wip: ObservableList<Image> = FXCollections.observableArrayList()
    val wipProperty = SimpleListProperty<Image>(wip)
    var references: ObservableList<Image> = FXCollections.observableArrayList()
    val referencesProperty = SimpleListProperty<Image>(references)
    var tags: ObservableList<String> = FXCollections.observableArrayList()
    val tagsProperty = SimpleListProperty<String>(tags)
    val sellingInformationProperty = SimpleObjectProperty<SellingInformationHolder>(SellingInformationHolder())
    var sellingInformation: SellingInformationHolder by sellingInformationProperty
    val finishingDateProperty = SimpleObjectProperty<LocalDate>()
    var finishingDate: LocalDate? by finishingDateProperty
    val finishedProperty = SimpleBooleanProperty()
    var finished by finishedProperty


    fun setSellingInformationOf(domain: SellingInformation?) {
        if (domain != null) sellingInformation = SellingInformationHolder().apply {
            sold = true
            purchaser = PurchaserHolder().apply {
                name = domain.purchaser.name
                address = domain.purchaser.address
            }
            price = domain.price
            date = domain.date
        }

    }
}


/** Holder representing the selling information */
@Suppress("KDocMissingDocumentation")
class SellingInformationHolder {
    val soldProperty = SimpleBooleanProperty(false)
    var sold by soldProperty
    val purchaserProperty = SimpleObjectProperty<PurchaserHolder>(PurchaserHolder())
    var purchaser: PurchaserHolder by purchaserProperty
    val priceProperty = SimpleDoubleProperty()
    var price: Double by priceProperty
    val dateProperty = SimpleObjectProperty<LocalDate>()
    var date: LocalDate by dateProperty
}

/** ViewModel for the selling information */
@Suppress("KDocMissingDocumentation", "unused")
class SellingInformationViewModel : ItemViewModel<SellingInformationHolder>() {
    val sold = bind { item?.soldProperty }
    val price = bind { item?.priceProperty }
    val date = bind { item?.dateProperty }
    val name = bind { item?.purchaser?.nameProperty }
    val address = bind { item?.purchaser?.addressProperty }
}


/** Holder for purchaser information */
@Suppress("KDocMissingDocumentation")
class PurchaserHolder {
    val nameProperty = SimpleStringProperty()
    var name: String by nameProperty
    val addressProperty = SimpleStringProperty()
    var address: String by addressProperty
}
