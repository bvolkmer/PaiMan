package de.x4fyr.paiman.lib.provider

import android.app.Activity
import android.content.Context
import android.content.CursorLoader
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import javafxports.android.FXActivity
import java.io.File


/**
 * Created by x4fyr on 3/22/17.
 * Taken an modified from https://gist.github.com/Mariovc/f06e70ebe8ca52fbbbe2
 */
class AndroidPictureProvider : PictureProvider {

    private val context: FXActivity = FXActivity.getInstance()

    companion object {
        private const val TAG = "ImagePicker"
        private const val TEMP_IMAGE_NAME = "tempImage"
        private const val PICK_IMAGE_ID = 234 //the number doesn't matter
    }

    /** Action on return of the picker */
    var onReturn: ((url: String) -> Unit)? = null


    init {
        FXActivity.getInstance().setOnActivityResultHandler { requestCode, resultCode, intent ->
            if (requestCode == PICK_IMAGE_ID)
                onReturn?.invoke(getUriFromResult(context, resultCode, intent))
        }

    }

    override fun pickPicture(onReturn: (url: String?) -> Unit) {
        this.onReturn = onReturn
        FXActivity.getInstance().startActivityForResult(getPickImageIntent(), PICK_IMAGE_ID)
    }


    /** Create the intent to launch the picker */
    fun getPickImageIntent(): Intent? {
        var chooserIntent: Intent? = null

        var intentList: MutableList<Intent> = ArrayList()

        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePhotoIntent.putExtra("return-data", true)
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(context)))
        intentList = addIntentsToList(context, intentList, pickIntent)
        intentList = addIntentsToList(context, intentList, takePhotoIntent)

        if (intentList.size > 0) {
            chooserIntent = Intent.createChooser(intentList.removeAt(intentList.size - 1), "Pick Image")
            chooserIntent!!.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toTypedArray<Parcelable>())
        }

        return chooserIntent
    }

    private fun addIntentsToList(context: Context, list: MutableList<Intent>, intent: Intent): MutableList<Intent> {
        val resInfo = context.packageManager.queryIntentActivities(intent, 0)
        for (resolveInfo in resInfo) {
            val packageName = resolveInfo.activityInfo.packageName
            val targetedIntent = Intent(intent)
            targetedIntent.`package` = packageName
            list.add(targetedIntent)
            Log.d(TAG, "Intent: " + intent.action + " package: " + packageName)
        }
        return list
    }

    private fun getTempFile(context: Context): File {
        val imageFile = File(context.externalCacheDir, TEMP_IMAGE_NAME)
        imageFile.parentFile.mkdirs()
        return imageFile
    }

    private fun getUriFromResult(context: Context, resultCode: Int, imageReturnedIntent: Intent?): String {
        val imageFile: File = getTempFile(context)
        var selectedImage: String? = null
        if (resultCode == Activity.RESULT_OK) {
            val intentDataString = imageReturnedIntent?.data?.toString()
            if (intentDataString == null || intentDataString.contains(imageFile.toString())) {
                /** CAMERA **/
                selectedImage = imageFile.path
            } else {
                /** ALBUM **/
                if (imageReturnedIntent.data.toString().contains("content:/")) {
                    val projection: Array<String> = Array(1, { MediaStore.Images.Media.DATA })
                    val cursor = CursorLoader(context, imageReturnedIntent.data, projection, null, null,
                            null).loadInBackground()
                    val idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    cursor.moveToFirst()
                    selectedImage = cursor.getString(idx)
                } else {
                    selectedImage = imageReturnedIntent.data.path
                }
            }
        }
        return selectedImage ?: ""
    }
}
