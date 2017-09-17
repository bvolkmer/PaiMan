@file:Suppress("KDocMissingDocumentation", "UNUSED_PARAMETER")

package de.x4fyr.paiman

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import dagger.android.AndroidInjection
import de.x4fyr.paiman.app.BaseActivity
import de.x4fyr.paiman.lib.adapter.GoogleDriveStorageAdapter
import de.x4fyr.paiman.lib.provider.AndroidPictureProvider
import de.x4fyr.paiman.lib.provider.ServiceProvider
import kotlinx.android.synthetic.main.activity_drive_test.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.image
import java.io.File
import javax.inject.Inject

class DriveTestActivity: BaseActivity() {

    @Inject lateinit var serviceProvider: ServiceProvider

    private lateinit var adapter: GoogleDriveStorageAdapter

    private val pictureProvider = AndroidPictureProvider(this)

    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        adapter = serviceProvider.storageAdapter
        setContentView(R.layout.activity_drive_test)
    }

    fun onSave(view: View) {
        launch(CommonPool) {
            Log.i("ImageName:", adapter.saveImage(File(imageUrl!!).inputStream()))
        }
    }

    fun onGet(view: View) {
        val id = idInput.text.toString()
        launch(CommonPool) {
            val stream = adapter.getImage(id)
            val drawable = Drawable.createFromStream(stream, id)
            launch(UI) {
                selectImage.image = drawable
            }
        }
    }

    fun onDelete(view: View) {
        launch(CommonPool) {
            adapter.deleteImage(idInput.text.toString())
        }
    }

    fun onSelectImage(view: View) {
        pictureProvider.pickPicture {
            imageUrl = it
            selectImage.image = Drawable.createFromPath(it!!)
        }
    }

}
