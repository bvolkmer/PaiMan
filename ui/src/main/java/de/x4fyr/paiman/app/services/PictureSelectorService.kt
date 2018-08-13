package de.x4fyr.paiman.app.services

import java.io.InputStream

/** Service handling system dependent picture selection */
interface PictureSelectorService {

    /** Open system dependent dialog to get a jpeg picture */
    fun pickPicture(onReturn: (InputStream?) -> Unit)
}