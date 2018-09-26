package de.x4fyr.paiman.app.adapter

import android.util.Base64

/**
 * Encoder using [android.util.Base64]
 */
class AndroidBase64Encoder : Base64Encoder() {
    override fun encodeToString(byteArray: ByteArray): String = Base64.encodeToString(byteArray, Base64.DEFAULT)
}