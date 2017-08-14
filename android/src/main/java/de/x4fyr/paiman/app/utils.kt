package de.x4fyr.paiman.app

import android.support.v4.app.DialogFragment
import android.view.MenuItem
import android.view.View

/** An dialog Fragment that has an apply action.
 *
 * This is necessary because the parent activity handles the menu item and button onClick event from this fragment, and
 * thous needs a target to redirect the onClick events
 */
abstract class ApplyingDialogFragment: DialogFragment() {
    /** Apply action on a button which is due to material design a menu item/button */
    abstract fun onApply(menuItem: MenuItem)

    /** Actions on other ordinary buttons of this dialog */
    abstract fun onDialogButton(view: View)
}
