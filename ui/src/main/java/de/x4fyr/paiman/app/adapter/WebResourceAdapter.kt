package de.x4fyr.paiman.app.adapter

/** Adapter providing access to ui/web resources */
interface WebResourceAdapter {

    /** Get content of [relativePath] as [String] */
    fun getResourceText(relativePath: String): String
}