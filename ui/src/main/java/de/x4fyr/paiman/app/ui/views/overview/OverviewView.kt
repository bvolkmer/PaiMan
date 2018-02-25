package de.x4fyr.paiman.app.ui.views.overview

import de.x4fyr.paiman.app.services.WebViewService
import de.x4fyr.paiman.app.ui.*
import de.x4fyr.paiman.app.ui.html.onsen.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import kotlinx.html.dom.document
import org.w3c.dom.Element
import java.util.Observable
import java.util.Observer

/** [View] including overview painting listing */
class OverviewView(private val model: OverviewModel): View, Observer {

    /** Controller controlling this view */
    lateinit var controller: OverviewController

    /** Update previews on db change
     * See [Observer.update] */
    override fun update(o: Observable?, arg: Any?) {
        launch(CommonPool) {
            refreshPreviews(element.await())
            try {
                controller.reload()
            } catch (e: UninitializedPropertyAccessException) {
            }
        }
    }

    init {
        model.addObserver(this)
    }

    override val element by lazy {
        async(CommonPool) {
            document { }.create.html {
                defaultHead()
                defaultBody({
                    div("center") {
                        +"Paiman"
                    }
                    div("right") {
                        onsButton(modifier = ONS_BUTTON.Modifier.QUIET,
                                onClick = "${WebViewService.javascriptModuleName}.refresh()") {
                            onsIcon("fa-sync")
                        }
                    }
                }, {
                    onsRow {
                        id = "previewRow"
                    }
                    onsFab(ripple = true, position = ONS_FAB.Position.BOTTOM_RIGHT,
                            onClick = "${WebViewService.javascriptModuleName}.openAddPainting()") {
                        onsIcon(icon = "fa-plus")
                    }
                })
            }.also {
                refreshPreviews(it)
            }
        }
    }

    private suspend fun refreshPreviews(base: Element) {
        val previews = model.previews.await()
        base.forEachTagWithId(ONS_ROW.tagName, "previewRow") {
            it.removeAllChildren()
            it.append {
                for ((paintingId, title, image) in previews) {
                    onsButton(modifier = ONS_BUTTON.Modifier.LIGHT) {
                        ripple = true
                        onClick = "${WebViewService.javascriptModuleName}.openPainting('$paintingId')"
                        onsCard(title = { +title }) {
                            id = "painting-$paintingId"
                            img(src = jpegDataString(image)) {
                                style = "max-width: 100px; max-height: 100px"
                            }
                        }
                    }
                }
            }
        }
    }
}
