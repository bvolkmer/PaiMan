package de.x4fyr.paiman.app.adapter

import java.util.*

/**
 * Encoder using [java.util.Base64]
 */
class JavaBase64Encoder : Base64Encoder() {

    private val encoder = Base64.getEncoder()
    override fun encodeToString(byteArray: ByteArray): String = encoder.encodeToString(byteArray)

}
