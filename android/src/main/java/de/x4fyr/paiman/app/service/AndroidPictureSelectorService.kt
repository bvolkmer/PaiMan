package de.x4fyr.paiman.app.service

import de.x4fyr.paiman.app.services.PictureSelectorService
import java.io.InputStream

class AndroidPictureSelectorService: PictureSelectorService {
    /** Open system dependent dialog to get a jpeg picture */
    override fun pickPicture(onReturn: (InputStream?) -> Unit) {
        TODO("not implemented")
    }
}