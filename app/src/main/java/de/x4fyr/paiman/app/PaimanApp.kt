package de.x4fyr.paiman.app

import de.x4fyr.paiman.lib.provider.ServiceProvider
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.stage.Screen
import javafx.stage.Stage
import java.util.ServiceLoader
import java.util.logging.Logger

/**
 * @author x4fyr
 * *         Created on 3/16/17.
 */
class PaimanApp : Application() {

    private val LOG = Logger.getLogger(PaimanApp::class.java.name)

    val serviceProvider: ServiceProvider by lazy {
        var result: ServiceProvider? = null
        val serviceLoader: ServiceLoader<ServiceProvider> = ServiceLoader.load(ServiceProvider::class.java)
        for (provider in serviceLoader.iterator()) {
            if (result == null) {
                result = provider
                LOG.info("Using ServiceProvider: ${provider.javaClass.name}")
            } else {
                break
            }
        }
        if (result != null) {
            result
        } else {
            throw RuntimeException("No ServiceProvider found!")
        }
    }

    @Throws(Exception::class)
    override fun start(stage: Stage) {
        val bounds = Screen.getPrimary().visualBounds
        stage.scene = Scene(StackPane(Label("Loading...")), bounds.width, bounds.height)
        stage.show()
    }

}
