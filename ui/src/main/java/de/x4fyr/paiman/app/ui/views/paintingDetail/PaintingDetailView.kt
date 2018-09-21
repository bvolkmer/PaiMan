package de.x4fyr.paiman.app.ui.views.paintingDetail

import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.*
import de.x4fyr.paiman.app.ui.html.onsen.*
import de.x4fyr.paiman.lib.domain.Painting
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import kotlinx.html.dom.document
import org.threeten.bp.LocalDate
import org.w3c.dom.Element

class PaintingDetailView(val model: PaintingDetailModel) : View {

    init {
        model.view = this
    }

    var controller: PaintingDetailController? = null

    fun update(id: ID) {
        launch {
            when (id) {
                ID.WIPS -> {
                    val wips = model.wips.await()
                    _element?.forEachTagWithId("ons-row", id.idString) { it ->
                        it.removeAllChildren()
                        it.append {
                            horizontalImageList(wips, "addWIP()")
                        }
                    }
                }
                ID.REFS -> {
                    val refs = model.refs.await()
                    _element?.forEachTagWithId("ons-row", id.idString) { it ->
                        it.removeAllChildren()
                        it.append {
                            horizontalImageList(refs, "addRef()")
                        }
                    }
                }
                ID.TAGS -> {
                    val tags = model.painting.await().tags
                    _element?.forEachTagWithId("div", id.idString) { elem ->
                        elem.removeAllChildren()
                        elem.append {
                            p {
                                +"Tags: "
                                tags.sortedBy { it }.forEachIndexed { index, tag ->
                                    if (index != 0) {
                                        +", "
                                    }
                                    a {
                                        +tag
                                    }
                                }
                                +"\t"
                                onsButton("showAddTagDialog()") {
                                    onsIcon("fa-plus")
                                    //TODO: add tag dialog
                                }
                            }
                        }
                    }
                }
                ID.FINISH -> {
                    val painting = model.painting.await()
                    if (painting.finished) {
                        _element?.forEachTagWithId("div", id.idString) { elem ->
                            elem.removeAllChildren()
                            elem.append {
                                p {
                                    +"Finished: ${painting.finishingDate!!.year}-${painting.finishingDate!!.monthValue}"
                                }

                            }
                        }
                    }
                }
            }
            controller?.loadView()
            //TODO: Fix update, that is not happening. Most probably because Deferred.await() creates a copy that is modified here
        }
    }

    private var _element: Element? = null

    /** Element to be shown on this view */
    override val element: Deferred<Element>
        get() = async {
            val el = _element
            return@async if (el != null) el
            else {
                val mainPicture = model.mainPicture.await()
                val painting: Painting = model.painting.await()
                val sellingInformation = painting.sellingInfo
                val wips = model.wips.await()
                val refs = model.refs.await()
                return@async document { }.create.html {

                    defaultHead {
                        script {
                            unsafe {
                                +("""var showDialog = function(id) {
                                    |var dialog = document.getElementById(id);
                                    |closeSide();
                                    |dialog.hidden = false;
                                    |dialog.show();
                                    |};
                                    |var hideDialog = function(id) {
                                    |var dialog = document.getElementById(id);
                                    |dialog.hidden = true;
                                    |dialog.hide();
                                    |};
                                    |var submitAddTagDialog = function() {
                                    |var dialog = document.getElementById('addTagDialog');
                                    |var input = document.getElementById('addTagInput');
                                    ${WebViewService.javascriptControllerModuleName}.addTag(input.value);
                                    |hideDialog('addTagDialog');
                                    |};
                                    |var submitFinishingDialog = function() {
                                    |var dialog = document.getElementById('finishDialog');
                                    |var year = document.getElementById('finishingYear');
                                    |var month = document.getElementById('finishingMonth');
                                    ${WebViewService.javascriptControllerModuleName}.finishing(year.value, month.value);
                                    |hideDialog('finishingDialog');
                                    |};
                                    |var openSide = function() {
                                    |var side = document.getElementById("sideMenu")
                                    |side.open();
                                    |}
                                    |var closeSide = function() {
                                    |var side = document.getElementById("sideMenu")
                                    |side.close();
                                    |}
                                    |""".trimMargin()).trimIndent()
                            }
                        }
                    }
                    splitterBody(side = "right", sideId = "sideMenu",
                            toolbarContent = {
                                div(classes = "left") {
                                    onsButton(modifier = ONS_BUTTON.Modifier.QUIET,
                                            onClick = "${WebViewService.javascriptControllerModuleName}.back()") {
                                        onsIcon("fa-arrow-left")
                                    }
                                }
                                div(classes = "right") {
                                    onsToolbarButton(onClick = "openSide()") {
                                        onsIcon("fa-bars")
                                    }
                                }
                            },
                            sideContent = {
                                onsList {
                                    onsListItem {
                                        +"Finish"
                                        onClick = "showDialog('finishDialog')"
                                    }
                                    onsListItem {
                                        +"Sell"
                                    }
                                }
                            }
                    ) {
                        div {
                            h1 {
                                +painting.title
                            }
                            img {
                                src = mainPicture
                            }
                        }
                        div {
                            id = ID.TAGS.idString
                            p {
                                +"Tags: "
                                painting.tags.sortedBy { it }.forEachIndexed { index, tag ->
                                    if (index != 0) {
                                        +", "
                                    }
                                    a {
                                        +tag
                                    }
                                }
                                +"\t"
                                onsButton("showDialog('addTagDialog')") {
                                    onsIcon("fa-plus")
                                    //TODO: add tag dialog
                                }
                            }
                            div {
                                id = ID.FINISH.idString
                                if (painting.finished) {
                                    p {
                                        +"Finished: ${painting.finishingDate!!.year}-${painting.finishingDate!!.monthValue}"
                                    }
                                }
                            }
                            if (sellingInformation != null) {
                                p {
                                    +"Sold on ${sellingInformation.date} at ${sellingInformation.price} to ${sellingInformation.purchaser.name}"
                                }
                            }
                        }
                        div {
                            h2 {
                                +"WIP"
                            }
                            onsRow {
                                id = ID.WIPS.idString
                                horizontalImageList(wips, "addWIP()")
                            }
                        }
                        div {
                            h2 {
                                +"References"
                            }
                            onsRow {
                                id = ID.REFS.idString
                                horizontalImageList(refs, "addRef()")
                            }
                        }
                        onsDialog {
                            id = "addTagDialog"
                            hidden = true
                            div {
                                style = "padding: 1em;"
                                h2 { +"Enter Tag" }
                                input(type = InputType.text) {
                                    id = "addTagInput"
                                    placeholder = "New tag"
                                }
                                br()
                                onsButton(onClick = "submitAddTagDialog()") {
                                    +"Add"
                                    style = "margin-top: 1em"
                                }
                            }
                        }
                        onsDialog {
                            id = "finishDialog"
                            hidden = true
                            div {
                                style = "padding: 1em;"
                                onsRow {
                                    h2 {
                                        +"Mark painting as finished"
                                    }
                                }
                                onsRow {
                                    +"Date:"
                                }
                                onsRow {
                                    style = "padding:1em;"
                                    val date = LocalDate.now()
                                    onsCol {
                                        onsInput(type = InputType.number) {
                                            +"Year"
                                            id = "finishingYear"
                                            value = date.year.toString()
                                        }
                                    }
                                    onsCol {
                                        onsInput(type = InputType.number) {
                                            +"Month"
                                            id = "finishingMonth"
                                            value = date.monthValue.toString()
                                        }
                                    }
                                }
                                onsRow {
                                    onsCol {
                                        onsButton(onClick = "submitFinishingDialog()") { +"Submit" }
                                    }
                                    onsCol {
                                        onsButton(onClick = "hideDialog('finishDialog')") {
                                            style = "float:right;"
                                            +"Cancel"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }.also { _element = it }
            }
        }


    private fun FlowContent.horizontalImageList(jpegDataStrings: List<String>, addOnClick: String) {
        jpegDataStrings.forEach {
            div {
                classes += "detailPictureListItemContainer"
                img {
                    classes += "detailPictureListItem"
                    src = it
                }
            }
        }
        div {
            classes += "detailPictureListItemContainer"
            //style = "display: flex; align-items: center"
            onsButton("${WebViewService.javascriptControllerModuleName}.$addOnClick") {
                style = "margin:auto;"
                onsIcon("fa-plus")
            }
        }
    }

    private fun <T, C : TagConsumer<T>> C.horizontalImageList(jpegDataStrings: List<String>, addOnClick: String) {
        jpegDataStrings.forEach {
            div {
                classes += "detailPictureListItemContainer"
                img {
                    classes += "detailPictureListItem"
                    src = it
                }
            }
        }
        div {
            classes += "detailPictureListItemContainer"
            //style = "display: flex; align-items: center"
            onsButton("${WebViewService.javascriptControllerModuleName}.$addOnClick") {
                style = "margin:auto;"
                onsIcon("fa-plus")
            }
        }
    }


    enum class ID(val idString: String) {
        WIPS("wips"), REFS("refs"), TAGS("tags"), FINISH("finish");

        override fun toString(): String {
            return idString
        }
    }
}
