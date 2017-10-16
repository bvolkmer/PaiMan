package de.x4fyr.paiman.lib.services

import java.util.logging.Logger

/**
 * Exception raised in Services that should/could be handled by GUI
 */
class ServiceException: Exception {
    init {
        //print message with the logger of the throwing class
        Logger.getLogger(super.getStackTrace()[0].className).info(message)
    }
    constructor(msg: String): super(msg)
    constructor(msg: String, cause: Throwable): super(msg, cause)
    constructor(): super()
}
