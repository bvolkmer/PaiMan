package de.x4fyr.paiman.lib.provider

import de.x4fyr.paiman.lib.provider.ServiceProvider
import javafxports.android.FXActivity
import com.couchbase.lite.android.AndroidContext

/**
 * @author x4fyr
 * Created on 3/17/17.
 */
class AndroidServiceProvider : ServiceProvider(AndroidContext(FXActivity.getInstance())) {
}