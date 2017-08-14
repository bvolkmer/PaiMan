package de.x4fyr.paiman

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import de.x4fyr.paiman.app.ApplyingDialogFragment
import de.x4fyr.paiman.lib.provider.PictureProvider
import de.x4fyr.paiman.lib.services.PaintingService
import kotlinx.android.synthetic.main.fragment_add_painting.*
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.image
import org.jetbrains.anko.imageResource
import java.io.File

/** Dialog to add new paintings */
class AddPaintingFragment: ApplyingDialogFragment() {

    private lateinit var paintingService: PaintingService
    private lateinit var pictureProvider: PictureProvider
    private lateinit var currentView: View

    private var model = Model(null, null)

    private val picturePickHandler: (String?) -> Unit = {
        Log.i("IMAGE_SELECT", "Got image $it")
        model.imageUrl = it
        if (it != null) {
            add_painting_main_image.image = Drawable.createFromPath(it)
        } else {
            add_painting_main_image.imageResource = android.R.drawable.ic_menu_report_image
        }
    }

    /** See [ApplyingDialogFragment] */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_painting, container, false).also {
            val toolbar = it.findViewById<Toolbar>(R.id.add_painting_toolbar)
            toolbar.inflateMenu(R.menu.menu_add_painting)
            toolbar.setTitle(R.string.add_painting_title)
        }
    }

    /** See [ApplyingDialogFragment] */
    override fun onStart() {
        (context as MainActivity).setActiveDialogFragment(this)
        if (model.imageUrl == null) pictureProvider.pickPicture(picturePickHandler)
        super.onStart()
    }

    /** See [ApplyingDialogFragment] */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = super.onCreateDialog(savedInstanceState)
            .also {
                it.requestWindowFeature(Window.FEATURE_NO_TITLE)
                paintingService = (context as MainActivity).serviceProvider.paintingService
                pictureProvider = (context as MainActivity).pictureProvider
            }

    /** See [ApplyingDialogFragment] */
    override fun onCreate(savedInstanceState: Bundle?) {
        paintingService = (context as MainActivity).serviceProvider.paintingService
        pictureProvider = (context as MainActivity).pictureProvider
        super.onCreate(savedInstanceState)
    }

    override fun onApply(menuItem: MenuItem) {
        model.title = add_painting_name.text!!.toString()

        when {
            model.title.isNullOrEmpty() -> errorDialog(R.string.add_painting_warning_name_missing)
            model.imageUrl.isNullOrEmpty() -> errorDialog(R.string.add_painting_warning_image_missing)
            else -> bg {
                var id: String? = null
                val mainPictureFile = File(model.imageUrl!!)
                if (mainPictureFile.exists() && mainPictureFile.canRead()) {
                    val inputStream = mainPictureFile.inputStream()
                    if (inputStream.available() > 0) {
                        Log.i("IMAGE_LOADING",
                                "InputStream of size ${inputStream.available()}: ${mainPictureFile.path}")
                        id = paintingService.composeNewPainting(title = model.title!!, mainPicture = inputStream).id
                    } else {
                        errorDialog(R.string.add_painting_warning_image_not_readable)
                        Log.w("IMAGE_LOADING", "Trying to read empty file: ${mainPictureFile.path}")
                    }
                } else {
                    errorDialog(R.string.add_painting_warning_image_not_readable)
                    Log.w("IMAGE_LOADING",
                            "Error: ${mainPictureFile.path} -> ${mainPictureFile.exists()} || ${mainPictureFile.canRead()}")
                }
                if (id != null) {
                    dismiss()
                } else {
                    errorDialog(R.string.add_painting_warning_no_id)
                    Log.w("IMAGE_LOADING", "Saving finished unsuccessful")
                }
            }
        }
    }

    override fun onDialogButton(view: View) {
        when {
            view.id == R.id.add_painting_main_image -> pictureProvider.pickPicture(picturePickHandler)
        }
    }

    private fun errorDialog(msg: Int) {
        AlertDialog.Builder(activity)
                .setMessage(msg)
                .create()
                .show()
    }

    /** Model containing data of the to be created painting */
    data class Model(var title: String?, var imageUrl: String?)

}


