package de.x4fyr.paiman.app

import de.x4fyr.paiman.app.ui.initialLoading.View
import de.x4fyr.paiman.lib.provider.ServiceProvider
import tornadofx.*
import java.util.ServiceLoader

/**
 * @author x4fyr
 * *         Created on 3/16/17.
 */
class PaimanApp : App(View::class)
