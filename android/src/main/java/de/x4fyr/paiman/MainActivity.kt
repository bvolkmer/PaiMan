package de.x4fyr.paiman

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import de.x4fyr.paiman.app.ApplyingDialogFragment
import de.x4fyr.paiman.lib.provider.AndroidPictureProvider
import de.x4fyr.paiman.lib.provider.AndroidServiceProvider
import de.x4fyr.paiman.lib.services.DesignService
import de.x4fyr.paiman.lib.services.PaintingService
import de.x4fyr.paiman.lib.services.QueryService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.displayMetrics
import org.jetbrains.anko.image
import org.jetbrains.anko.support.v4.onRefresh
import java.io.InputStream

/** Main Activity of the app, which is launched first and shows a list of current paintings
 *
 * It initialises the different providers.
 */
class MainActivity: AppCompatActivity() {

    /** Initial [AndroidServiceProvider] */
    lateinit var serviceProvider: AndroidServiceProvider
    /** Initial [AndroidPictureProvider] */
    lateinit var pictureProvider: AndroidPictureProvider
    private lateinit var queryService: QueryService
    private lateinit var paintingService: PaintingService
    private lateinit var designService: DesignService

    private var onActivityResultHandler: ((Int, Int, Intent?) -> Unit)? = null
    private var activeDialogFragment: ApplyingDialogFragment? = null

    private lateinit var gridAdapter: GridAdapter
    private var models: MutableList<Model> = mutableListOf()
    private var selectedPositions: MutableSet<Int> = hashSetOf()

    private val actionModeCallback = object: ActionMode.Callback {
        /** See [ActionMode.Callback] */
        override fun onActionItemClicked(mode: ActionMode, menuItem: MenuItem): Boolean {
            when {
                menuItem.itemId == R.id.main_selection_delete -> {
                    selectedPositions.forEach { paintingService.delete(models[it].id) }
                    mode.finish()
                    Snackbar.make(main_layout, R.string.main_delete_notification, Snackbar.LENGTH_SHORT).show()
                    reloadContent()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        serviceProvider = AndroidServiceProvider(this)
        pictureProvider = AndroidPictureProvider(this)
        queryService = serviceProvider.queryService
        paintingService = serviceProvider.paintingService
        designService = serviceProvider.designService
        gridAdapter = GridAdapter(models, selectedPositions, resources)

        val spanCount = Math.floor(
                displayMetrics.widthPixels/TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 106.toFloat(),
                        displayMetrics).toDouble()).toInt()
        paintingGrid.layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
        paintingGrid.adapter = gridAdapter

        fab.setOnClickListener { _ ->
            val fragment: DialogFragment = AddPaintingFragment()
            // if (designService.isLargeDevice) {
            fragment.show(supportFragmentManager, "dialog")
            /*} else {
                supportFragmentManager.beginTransaction().apply {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    add(android.R.id.content, fragment)
                    addToBackStack(null)
                    commit()
                }
            }*/
        }
        // Register query listener
        queryService.allPaintingsQuery.toLiveQuery().apply {
            addChangeListener {
                async(UI) {
                    swipeRefreshLayout.isRefreshing = true
                    val newModels = async(CommonPool) {
                        if (it.rows != null) {
                            paintingService.getFromQueryResult(it.rows!!).map {
                                Model(id = it.id, mainImage = paintingService.getPictureStream(it.mainPicture, it),
                                        title = it.title, mainImageId = it.mainPicture.id)
                            }
                        } else listOf()
                    }
                    models.clear()
                    models.addAll(newModels.await())
                    gridAdapter.notifyDataSetChanged()
                    swipeRefreshLayout.isRefreshing = false
                }
            }
            start()
        }
        swipeRefreshLayout.onRefresh { reloadContent(completionHandler = { swipeRefreshLayout.isRefreshing = false }) }
    }


    /**
     *
     */
    private fun reloadContent(completionHandler: () -> Unit = {}) {
        async(UI) {
            val newModels = async(CommonPool) {
                paintingService.getFromQueryResult(queryService.allPaintingsQuery.run()).map {
                    Model(id = it.id, mainImage = paintingService.getPictureStream(it.mainPicture, it),
                            title = it.title, mainImageId = it.mainPicture.id)
                }
            }
            models.clear()
            models.addAll(newModels.await())
            gridAdapter.notifyDataSetChanged()
            completionHandler()
        }
    }

    /** See [AppCompatActivity] */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        onActivityResultHandler?.invoke(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    /** Set the handler handling results of activities */
    fun setOnActivityResultHandler(handler: (requestCode: Int, resultCode: Int, data: Intent?) -> Unit) {
        onActivityResultHandler = handler
    }

    /** Set the current active dialog fragment */
    fun setActiveDialogFragment(dialogFragment: ApplyingDialogFragment) {
        activeDialogFragment = dialogFragment
    }

    /** The apply action on the current active dialog fragment */
    fun onApply(menuItem: MenuItem) {
        activeDialogFragment?.onApply(menuItem)
    }

    /** Button action on the current active dialog fragment */
    fun onDialogButton(view: View) {
        activeDialogFragment?.onDialogButton(view)
    }

}


private class GridAdapter(val models: MutableList<Model>, val selectedPositions: Set<Int>, val resources: Resources)
    : RecyclerView.Adapter<GridAdapter.ModelViewHolder>() {

    /** See [RecyclerView.Adapter] */
    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        val model = models[position]
        async(UI) {
            val imageDeferred: Deferred<BitmapDrawable> = async(CommonPool) {
                BitmapDrawable(resources,
                        Bitmap.createScaledBitmap(BitmapFactory.decodeStream(model.mainImage), 100, 100, false))
            }
            holder.title.text = model.title
            if (selectedPositions.contains(position)) {
                holder.container.cardElevation = holder.container.maxCardElevation
                //holder.container.setCardBackgroundColor(resources.getColor(R.color.cardview_dark_background))
            } else {
                holder.container.cardElevation = 2.toFloat()
                //holder.container.setCardBackgroundColor(resources.getColor(R.color.cardview_light_background))
            }
            val image: BitmapDrawable = imageDeferred.await()
            holder.mainImage.image = image
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
        }

    }

}

private data class Model(val id: String, val mainImage: InputStream, val title: String, val mainImageId: String)
