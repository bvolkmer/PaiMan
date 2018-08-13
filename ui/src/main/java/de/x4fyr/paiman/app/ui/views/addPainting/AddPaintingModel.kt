package de.x4fyr.paiman.app.ui.views.addPainting

import de.x4fyr.paiman.lib.domain.SavedPainting
import de.x4fyr.paiman.lib.services.PaintingService
import de.x4fyr.paiman.lib.services.ServiceException
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.Base64
import java.util.Observable
import kotlin.properties.Delegates

/** Model for AddPainting MVC */
class AddPaintingModel(private val paintingService: PaintingService): Observable() {


    private lateinit var picture: ByteArrayInputStream
    /** picture in base64 encoding */
    var base64Picture: String? = null
        private set
    private val base64Encoder = Base64.getUrlEncoder()
    /** Title of painting */
    var title: String by Delegates.observable("") { _, oldValue, newValue ->
        if (oldValue != newValue) {
            setChanged()
            notifyObservers()
        }
    }

    /** Set picture */
    fun setPicture(inputStream: InputStream) {
        if (inputStream is ByteArrayInputStream) {
            picture = inputStream
            base64Picture = base64Encoder.encodeToString(picture.readBytes())
            picture.reset()
        } else {
            val byteArray = inputStream.readBytes()
            picture = ByteArrayInputStream(byteArray)
            base64Picture = base64Encoder.encodeToString(byteArray)
        }
        setChanged()
        notifyObservers()
    }

    /** Save painting to persistence */
    fun save(onSuccess: (SavedPainting) -> Unit = {}, onFailure: (ServiceException) -> Unit = {}) {
        launch(CommonPool) {
            try {
                //TODO: Add incomplete model exception
                onSuccess(paintingService.composeNewPainting(title, picture))
            } catch (e: ServiceException) {
                onFailure(e)
            }
        }
    }
}

