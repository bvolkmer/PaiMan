package de.x4fyr.paiman.app

/**
 * PlatformService implementation for android
 */
class AndroidService : PlatformService {

    /** Run platform dependent code previous to creating the user interface */
    override fun preUI() {
    }

    override fun startProfiling(name: String) {
        //val path = "${FXActivity.getInstance().getExternalFilesDir(null)}/$name"
        //Log.d("Tracing", "Trace log in file $path")
        //Debug.startMethodTracing(path)
    }

    override fun stopProfiling() {
        //Debug.stopMethodTracing()
    }
}