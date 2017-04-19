package de.x4fyr.paiman.lib.provider

import javafxports.android.FXActivity

/**
 * @author x4fyr
 * Created on 3/17/17.
 */
class AndroidServiceProvider : ServiceProvider(AndroidContext(FXActivity.getInstance()))