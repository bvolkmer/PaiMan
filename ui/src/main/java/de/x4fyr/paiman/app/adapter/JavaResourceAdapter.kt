package de.x4fyr.paiman.app.adapter

/** Pure Java implementation of [WebResourceAdapter]
 *
 * Uses [Class.getResourceAsStream] to load resource
 */
class JavaResourceAdapter: WebResourceAdapter {
    //override fun getResourceURL(relativePath: String): String = this.javaClass.getResource(relativePath).toExternalForm()

    /** See [WebResourceAdapter.getResourceText] */
    override fun getResourceText(relativePath: String): String = this.javaClass.getResourceAsStream(relativePath)
            .reader().readText()
}