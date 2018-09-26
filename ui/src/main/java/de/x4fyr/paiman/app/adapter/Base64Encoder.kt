package de.x4fyr.paiman.app.adapter

import java.io.InputStream

/**
 * Base64 encoder interface
 */
abstract class Base64Encoder {

    /**
     * Encode [byteArray] to [String]
     */
    abstract fun encodeToString(byteArray: ByteArray): String

    internal fun jpegDataString(base64Image: String) = "data:image/jpeg;base64,$base64Image"

    internal fun jpegDataString(stream: InputStream) = jpegDataString(encodeToString(stream.readBytes()))
}