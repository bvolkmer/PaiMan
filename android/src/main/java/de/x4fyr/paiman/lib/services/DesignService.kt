package de.x4fyr.paiman.lib.services

import android.content.Context
import de.x4fyr.paiman.R

/**
 * Service serving design information and actions
 */
class DesignService(context: Context) {
    /** Whether this device should be considered large, i.e. a tablet */
    val isLargeDevice: Boolean = context.resources.getBoolean(R.bool.large_layout)
}