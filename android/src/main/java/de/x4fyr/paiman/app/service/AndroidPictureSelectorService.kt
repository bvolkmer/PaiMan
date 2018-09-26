package de.x4fyr.paiman.app.service

import android.app.Activity
import android.content.Context
import android.content.CursorLoader
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import de.x4fyr.paiman.app.WebViewWrapperActivity
import de.x4fyr.paiman.app.services.PictureSelectorService
import java.io.File
import java.io.InputStream

/**
 * Taken an modified from https://gist.github.com/Mariovc/f06e70ebe8ca52fbbbe2
 */
class AndroidPictureSelectorService(private var owningActivity: WebViewWrapperActivity) : PictureSelectorService {

    companion object {
        private const val TAG = "ImagePicker"
        private const val TEMP_IMAGE_NAME = "tempImage"
        private const val PICK_IMAGE_ID = 234 //the number doesn't matter
    }

    /** Action on return of the picker */
    private var onReturn: ((stream: InputStream?) -> Unit)? = null


    init {
        owningActivity.addActivityResultHandler(this) { requestCode, resultCode, intent ->
            if (requestCode == PICK_IMAGE_ID) {
                val url = getUrlFromResult(owningActivity, resultCode, intent)
                val stream = getInputStreamFromUrl(url)
                onReturn?.invoke(stream)
            }
        }
    }

    override fun pickPicture(onReturn: (stream: InputStream?) -> Unit) {
        owningActivity.startActivityForResult(getPickImageIntent(), PICK_IMAGE_ID)
        this.onReturn = onReturn
    }


    private fun getPickImageIntent(): Intent? {
        var chooserIntent: Intent? = null

        var intentList: MutableList<Intent> = ArrayList()

        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePhotoIntent.putExtra("return-data", true)
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(owningActivity)))
        intentList = addIntentsToList(owningActivity, intentList, pickIntent)
        intentList = addIntentsToList(owningActivity, intentList, takePhotoIntent)

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
        val imageFile = File(context.externalCacheDir ?: context.cacheDir, TEMP_IMAGE_NAME)
        imageFile.parentFile.mkdirs()
        return imageFile
    }

    private fun getUrlFromResult(context: Context, resultCode: Int, imageReturnedIntent: Intent?): String {
        val imageFile: File = getTempFile(context)
        var selectedImage: String? = null
        if (resultCode == Activity.RESULT_OK) {
            val intentDataString = imageReturnedIntent?.data?.toString()
            if (intentDataString == null || intentDataString.contains(imageFile.toString())) {
                /** CAMERA **/
                selectedImage = imageFile.path
            } else {
                /** ALBUM **/
                selectedImage = if (imageReturnedIntent.data.toString().contains("content:/")) {
                    val projection: Array<String> = Array(1) { MediaStore.Images.Media.DATA }
                    val cursor = CursorLoader(context, imageReturnedIntent.data, projection, null, null,
                            null).loadInBackground()
                    val idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    cursor.moveToFirst()
                    cursor.getString(idx)
                } else {
                    imageReturnedIntent.data.path
                }
            }
        }
        return selectedImage ?: ""
    }


    private fun getInputStreamFromUrl(url: String): InputStream? = File(url)
            .takeIf { it.exists() && it.canRead() }
            ?.inputStream()?.takeIf { it.available() > 0 }
}
