package de.x4fyr.paiman.lib.provider

import com.couchbase.lite.JavaContext

/**
 * ServiceProvider implementation for desktop platform
 */
class DesktopServiceProvider : ServiceProvider(JavaContext())