package de.x4fyr.paiman.lib.provider

import javafxports.android.FXActivity
import com.couchbase.lite.android.AndroidContext //Here might be a false-positive error of intellij, do not remove

/**
 * @author x4fyr
 * Created on 3/17/17.
 */
class AndroidServiceProvider : ServiceProvider(AndroidContext(FXActivity.getInstance()))