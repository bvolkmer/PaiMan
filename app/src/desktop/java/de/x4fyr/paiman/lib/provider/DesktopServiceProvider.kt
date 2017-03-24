package de.x4fyr.paiman.lib.provider

import com.couchbase.lite.JavaContext
import de.x4fyr.paiman.lib.provider.ServiceProvider

/**
 * Created by x4fyr on 3/24/17.
 */
class DesktopServiceProvider: ServiceProvider(JavaContext()) {
}