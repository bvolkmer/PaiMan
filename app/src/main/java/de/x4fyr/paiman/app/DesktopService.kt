package de.x4fyr.paiman.app

/**
 * PlatformService implementation for desktop
 */
class DesktopService : PlatformService {
    /** Run platform dependent code previous to creating the user interface */
    override fun preUI() {
    }

    /** Start profiling on this platform
     * @param name Name of this profiling instance
     */
    override fun startProfiling(name: String) {
    }

    /** End profiling on this platform */
    override fun stopProfiling() {
    }
}