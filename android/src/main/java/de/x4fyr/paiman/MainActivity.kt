package de.x4fyr.paiman

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import de.x4fyr.paiman.app.BaseActivity
import de.x4fyr.paiman.app.errorDialog
import de.x4fyr.paiman.app.getInputStreamFromUrl
import de.x4fyr.paiman.lib.provider.AndroidPictureProvider
import de.x4fyr.paiman.lib.provider.AndroidServiceProvider
import de.x4fyr.paiman.lib.services.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.displayMetrics
import org.jetbrains.anko.image
import org.jetbrains.anko.support.v4.onRefresh
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import kotlin.concurrent.withLock

/** Main Activity of the app, which is launched first and shows a list of current paintings
 *
 * It initialises the different providers.
 */
class MainActivity: BaseActivity(), HasActivityInjector {

    /** [DispatchingAndroidInjector] for dependency injection */
    @Inject lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    /** See [HasActivityInjector.activityInjector] */
    override fun activityInjector(): AndroidInjector<Activity> = dispatchingActivityInjector

    /** Initial [AndroidServiceProvider] */
    @Inject lateinit var serviceProvider: AndroidServiceProvider
    /** Initial [AndroidPictureProvider] */
    private lateinit var pictureProvider: AndroidPictureProvider
    /** [QueryService] instance*/
    @Inject lateinit var queryService: QueryService
    /** [PaintingService] instance */
    @Inject lateinit var paintingService: PaintingService
    /** [DesignService] instance */
    @Inject lateinit var designService: DesignService
    /** [UpdateService] instance */
    @Inject lateinit var updateService: UpdateService

    private lateinit var gridAdapter: GridAdapter
    private var models: MutableList<Model> = mutableListOf()
    private var modelsLock = ReentrantLock()
    private var selectedPositions: MutableSet<Int> = hashSetOf()

    companion object {
        const val READ_EXTERNAL_PERMISSION_REQUEST_CODE = 5477
    }

    private val actionModeCallback = object: ActionMode.Callback {
        /** See [ActionMode.Callback] */
        override fun onActionItemClicked(mode: ActionMode, menuItem: MenuItem): Boolean {
            when {
                menuItem.itemId == R.id.main_selection_delete -> {
                    launch(CommonPool) {
                        selectedPositions.forEach {
                            try {
                                paintingService.delete(models[it].id)
                            } catch (e: ServiceException) {
                                errorDialog(R.string.error_removing_painting, e)
                            }
                        }
                        launch(UI) {
                            mode.finish()
                            Snackbar.make(main_layout, R.string.main_delete_notification, Snackbar.LENGTH_SHORT).show()
                            reloadContent()
                        }
                    }
                    return true
                }
            }
            return false
        }

        /** See [ActionMode.Callback] */
        override fun onCreateActionMode(mode: ActionMode, menu: Menu?): Boolean {
            mode.menuInflater.inflate(R.menu.selection_menu_main, menu)
            return true
        }

        /** See [ActionMode.Callback] */
        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

        /** See [ActionMode.Callback] */
        override fun onDestroyActionMode(mode: ActionMode?) {
            selectedPositions.clear()
            gridAdapter.notifyDataSetChanged()
            actionMode = null
        }


    }
    private var actionMode: ActionMode? = null

    /** Actions that happen on long click on a painting in the grid.
     *
     * Mainly selecting the item and enter/exit selection mode
     */
    val onPaintingLongClick: (id: Int) -> Unit = { id ->
        //if (actionMode != null) return
        if (selectedPositions.contains(id))
            selectedPositions.remove(id)
        else
            selectedPositions.add(id)
        gridAdapter.notifyDataSetChanged()
        val selectedCount = selectedPositions.size
        if (selectedCount > 0) {
            if (actionMode == null) {
                actionMode = startActionMode(actionModeCallback)
            }
        } else
            actionMode?.finish()
        actionMode?.title = getString(R.string.main_selection_title, selectedCount)
    }

    private fun updateDialogBuilder(): AlertDialog.Builder =
            AlertDialog.Builder(this).apply {
                setTitle(R.string.update_dialog_title)
                val view = layoutInflater.inflate(R.layout.dialog_update, null, false) as ViewGroup
                view.findViewById<TextView>(R.id.update_dialog_current_version).apply {
                    text = getString(R.string.update_dialog_current_version, updateService.currentVersion)
                }
                view.findViewById<TextView>(R.id.update_dialog_upstream_version).apply {
                    text = getString(R.string.update_dialog_upstream_version,
                            getString(R.string.update_dialog_upstream_version_pending))
                    launch(UI) {
                        val latestVersion = updateService.latestVersion.await()
                        if (latestVersion != null) {
                            text = getString(R.string.update_dialog_upstream_version, latestVersion)
                        }
                    }
                }
                view.findViewById<Button>(R.id.update_dialog_update_button).apply {
                    isEnabled = false
                    setOnClickListener { updateService.requestUpdate() }
                    launch(UI) {
                        isEnabled = !(updateService.isNewestVersion() ?: true)
                    }
                }
                setView(view)
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        pictureProvider = AndroidPictureProvider(this)
        gridAdapter = GridAdapter(models, selectedPositions, resources)

        paintingGrid.apply {
            val spanCount = Math.floor(
                    displayMetrics.widthPixels/TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 106.toFloat(),
                            displayMetrics).toDouble()).toInt()
            layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            adapter = gridAdapter
        }

        fab.setOnClickListener { _ ->
            AlertDialog.Builder(this).apply {
                data class Model(var title: String? = null, var imageUrl: String? = null)

                val model = Model()
                val view = layoutInflater.inflate(R.layout.dialog_add_painting, null, false) as ViewGroup
                val imageButton = view.findViewById<ImageButton>(R.id.add_painting_main_image).apply {
                    setOnClickListener {
                        pictureProvider.pickPicture {
                            Log.i("IMAGE_SELECT", "Got image $it")
                            if (!it.isNullOrEmpty()) {
                                model.imageUrl = it
                                image = Drawable.createFromPath(it)
                            }
                        }
                    }
                }
                val titleInput = view.findViewById<EditText>(R.id.add_painting_name)
                setView(view)
                setCancelable(true)
                setPositiveButton(R.string.add_painting_apply) { dialog, _ ->
                    model.title = titleInput.text!!.toString()
                    when {
                        model.title.isNullOrEmpty() -> errorDialog(R.string.add_painting_warning_name_missing)
                        model.imageUrl.isNullOrEmpty() -> errorDialog(R.string.add_painting_warning_image_missing)
                        else -> launch(CommonPool) {
                            val inputStream = getInputStreamFromUrl(model.imageUrl!!)
                            if (inputStream != null) {
                                try {
                                    paintingService.composeNewPainting(title = model.title!!,
                                            mainPicture = inputStream).id
                                } catch (e: ServiceException) {
                                    this@MainActivity.errorDialog(R.string.error_painting_save, e)
                                }
                                dialog.dismiss()
                            } else {
                                errorDialog(R.string.add_painting_warning_no_id)
                                Log.w("IMAGE_LOADING", "Saving finished unsuccessful")
                            }
                        }
                    }
                }
                pictureProvider.pickPicture {
                    Log.i("IMAGE_SELECT", "Got image $it")
                    if (!it.isNullOrEmpty()) {
                        model.imageUrl = it
                        imageButton.image = Drawable.createFromPath(it)
                    }
                }
            }.create().show()
        }
        // Register query listener
        queryService.allPaintingsQuery.toLiveQuery().apply {
            addChangeListener {
                async(UI) {
                    swipeRefreshLayout.isRefreshing = true
                    val newModels = if (it.rows != null) {
                        paintingService.getFromQueryResult(it.rows!!).map {
                            Model(id = it.id, mainImage = async(CommonPool) {
                                try {
                                    designService.getOrLoadThumbnailBitmap(it.mainPicture)
                                } catch (e: ServiceException) {
                                    this@MainActivity.errorDialog(R.string.error_image_load, e)
                                    null
                                }
                            }, title = it.title, mainImageId = it.mainPicture.id)
                        }
                    } else listOf()
                    modelsLock.withLock {
                        models.clear()
                        models.addAll(newModels)
                    }
                    gridAdapter.notifyDataSetChanged()
                    swipeRefreshLayout.isRefreshing = false
                }
            }
            start()
        }
        swipeRefreshLayout.onRefresh { reloadContent(completionHandler = { swipeRefreshLayout.isRefreshing = false }) }
        launch(UI)
        {
            if (updateService.isNewestVersion() == false) {
                updateDialogBuilder().create().show()
            }
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission
                .READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_PERMISSION_REQUEST_CODE)
        }
    }

    /** See [Activity.onRequestPermissionsResult]] */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == READ_EXTERNAL_PERMISSION_REQUEST_CODE) {
            if (!grantResults.isNotEmpty()
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        READ_EXTERNAL_PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        reloadContent { }
    }

    /**
     *
     */
    fun reloadContent(completionHandler: () -> Unit = {}) {
        async(UI) {
            val newModels = paintingService.getFromQueryResult(queryService.allPaintingsQuery.run()).map {
                Model(id = it.id, title = it.title, mainImageId = it.mainPicture.id,
                        mainImage = async(CommonPool) {
                            try {
                                designService.getOrLoadThumbnailBitmap(it.mainPicture)
                            } catch (e: ServiceException) {
                                this@MainActivity.errorDialog(R.string.error_image_load, e)
                                null
                            }
                        })
            }
            modelsLock.withLock {
                models.clear()
                models.addAll(newModels)
            }
            gridAdapter.notifyDataSetChanged()
            completionHandler()
        }
    }

    /** See [AppCompatActivity] */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /** Handler for menu clicks */
    fun onMenuClick(item: MenuItem) {
        when {
            item.itemId == R.id.menu_main_update -> updateDialogBuilder().create().show()
        }
    }

    /** Open detail activity */
    fun openDetailActivity(pos: Int) {
        startActivity(Intent(this, PaintingDetailActivity::class.java).apply {
            putExtra(PaintingDetailActivity.ID, models[pos].id)
        })
    }

}


private class GridAdapter(val models: MutableList<Model>, val selectedPositions: Set<Int>, val resources: Resources)
    : RecyclerView.Adapter<GridAdapter.ModelViewHolder>() {

    /** See [RecyclerView.Adapter] */
    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        val model = models[position]
        holder.title.text = model.title
        if (selectedPositions.contains(position)) {
            holder.container.cardElevation = holder.container.maxCardElevation
            //holder.container.setCardBackgroundColor(resources.getColor(R.color.cardview_dark_background))
        } else {
            holder.container.cardElevation = 5.toFloat()
            //holder.container.setCardBackgroundColor(resources.getColor(R.color.cardview_light_background))
        }
        launch(CommonPool) {
            val image = BitmapDrawable(resources, model.mainImage.await())
            launch(UI) {
                holder.mainImage.image = image
            }
        }
    }

    /** See [RecyclerView.Adapter] */
    override fun getItemCount(): Int = models.size

    /** See [RecyclerView.Adapter] */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder
            = ModelViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_main, parent, false))

    private class ModelViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        /** [CardView] container */
        var container: CardView = itemView.findViewById(R.id.painting_preview_card)
        /** [ImageView] containing the main image preview */
        var mainImage: ImageView = itemView.findViewById(R.id.painting_preview_image)
        /** [TextView] containing the title */
        var title: TextView = itemView.findViewById(R.id.painting_preview_title)

        init {
            container.setOnLongClickListener {
                (container.context as MainActivity).onPaintingLongClick(adapterPosition)
                false
            }

            container.setOnClickListener {
                (container.context as MainActivity).openDetailActivity(adapterPosition)
            }
        }

    }

}


private data class Model(val id: String, val mainImage: Deferred<Bitmap?>, val title: String, val mainImageId:
String)