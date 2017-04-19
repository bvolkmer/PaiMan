package de.x4fyr.paiman.app

import java.util.*

/**
 * Service running general platform dependent code
 */
interface PlatformService {

    companion object {
        /** Load platform dependent provider or services classes */
        fun <T : Any> loadProvider(clazz: Class<T>): T {
            var result: T? = null
            val serviceLoader: ServiceLoader<out T> = ServiceLoader.load(clazz)
            for (provider in serviceLoader.iterator()) {
                if (result == null) {
                    result = provider
                } else {
                    break
                }
            }
            return result ?: throw RuntimeException("No ServiceProvider found!")
        }
    }


    /** Run platform dependent code previous to creating the user interface */
    fun preUI(): Unit

    /** Start profiling on this platform
     * @param name Name of this profiling instance
     */
    fun startProfiling(name: String = "main"): Unit

    /** End profiling on this platform */
    fun stopProfiling(): Unit
}