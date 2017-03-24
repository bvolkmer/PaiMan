package de.x4fyr.paiman.lib.services

import java.util.logging.Logger

/**
 * Created by x4fyr on 3/22/17.
 */
class ServiceException(message: String): RuntimeException(message) {

    init {
        //print message with the logger of the throwing class
        Logger.getLogger(super.getStackTrace()[0].className).info(message)
    }
}

