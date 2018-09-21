package de.x4fyr.paiman.app.ui.views.addPainting

import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.*
import de.x4fyr.paiman.app.ui.html.onsen.ONS_BUTTON
import de.x4fyr.paiman.app.ui.html.onsen.onsButton
import de.x4fyr.paiman.app.ui.html.onsen.onsIcon
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import kotlinx.html.dom.document
import java.util.*

/** View for AddPainting MVC */
class AddPaintingView(val model: AddPaintingModel): View {

    /** Controller to control this view */
    lateinit var controller: AddPaintingController

    init {
        model.view = this
    }

    /** Update view on model changes
     * See [Observer.update] */
    fun update() {
        launch(CommonPool) {
            val updateImage = async {
                val picture = model.base64Picture
                element.await().forEachTagWithId("div", "mainImage") {
                    it.removeAllChildren()
                    if (picture != null) it.append {
                        img {
                            src = jpegDataString(picture)
                            style = "max-width: 100%; max-height: 100%"
                        }
                    }
                }
            }
            val updateTitle = async {
                element.await().forEachTagWithId("input", "titleInput") {
                    it.setAttribute("value", model.title)
                }
            }
            updateImage.await()
            updateTitle.await()
            controller.reload()
        }
    }

    /** See [View.element] */
    override val element by lazy {
        async {
            document { }.create.html {
                defaultHead()
                defaultBody({
                    div(classes = "left") {
                        onsButton(modifier = ONS_BUTTON.Modifier.QUIET,
                                onClick = "${WebViewService.javascriptControllerModuleName}.cancel()") {
                            onsIcon("fa-times")
                        }
                    }
                    div(classes = "right") {
                        onsButton(modifier = ONS_BUTTON.Modifier.QUIET,
                                onClick = "${WebViewService.javascriptControllerModuleName}.save()") {
                            onsIcon("fa-check")
                        }
                    }
                }, {
                    +"Title:"
                    input(InputType.text) {
                        id = "titleInput"
                        title = "Title"
                        placeholder = "Title"
                        onChange = "${WebViewService.javascriptControllerModuleName}.setTitle(this.value)"
                    }
                    onsButton("${WebViewService.javascriptControllerModuleName}.addPicture()", ONS_BUTTON.Modifier.LARGE) {
                        +"Add Photo"
                    }
                    div {
                        id = "mainImage"
                    }
                })
            }
        }
    }

}
