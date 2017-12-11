package de.x4fyr.paiman

import android.app.Activity
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import de.x4fyr.paiman.app.BaseActivity
import de.x4fyr.paiman.app.errorDialog
import de.x4fyr.paiman.app.getInputStreamFromUrl
import de.x4fyr.paiman.lib.domain.Purchaser
import de.x4fyr.paiman.lib.domain.SavedPainting
import de.x4fyr.paiman.lib.provider.AndroidPictureProvider
import de.x4fyr.paiman.lib.services.DesignService
import de.x4fyr.paiman.lib.services.PaintingService
import de.x4fyr.paiman.lib.services.PictureService
import de.x4fyr.paiman.lib.services.ServiceException
import kotlinx.android.synthetic.main.activity_painting_detail.*
import kotlinx.android.synthetic.main.content_painting_detail.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.displayMetrics
import org.jetbrains.anko.image
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onLongClick
import org.jetbrains.anko.support.v4.onRefresh
import org.threeten.bp.LocalDate
import javax.inject.Inject

/** Activity to show and edit a paintings details */
class PaintingDetailActivity: BaseActivity(), HasActivityInjector, HasSupportFragmentInjector {

    /** [DispatchingAndroidInjector] for activity dependency injection */
    @Inject lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>
    /** [DispatchingAndroidInjector] for fragment dependency injection */
    @Inject lateinit var dispatchingFragmentInjector: DispatchingAndroidInjector<Fragment>

    /** See [HasActivityInjector.activityInjector] */
    override fun activityInjector(): AndroidInjector<Activity> = dispatchingActivityInjector

    /** Returns an [AndroidInjector] of [Fragment]s.  */
    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingFragmentInjector

    /** Injected */
    @Inject lateinit var paintingService: PaintingService
    /** Injected */
    @Inject lateinit var designService: DesignService
    private lateinit var pictureProvider: PictureService

    companion object {
        /** ID identifier string for intents */
        const val ID = "id"
    }

    private lateinit var id: String
    private lateinit var model: DetailModel

    private lateinit var wipGridAdapter: WIPGridAdapter
    private lateinit var refGridAdapter: RefGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        id = intent.getStringExtra(ID)
        pictureProvider = AndroidPictureProvider(this)
        loadModel()
        setContentView(R.layout.activity_painting_detail)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        fab.setOnClickListener { _ ->
            AlertDialog.Builder(this@PaintingDetailActivity).apply {
                setTitle(R.string.edit_painting_title)
                var imageDirty = false
                var url = ""
                val contentView = layoutInflater.inflate(R.layout.dialog_edit_painting, null) as ScrollView
                contentView.findViewById<ImageButton>(
                        R.id.edit_painting_main_image_button).apply {
                    launch(UI) {
                        image = BitmapDrawable(resources, model.mainImage.await())
                    }
                    setOnClickListener { button ->
                        pictureProvider.pickPicture {
                            if (it != null) {
                                imageDirty = true
                                url = it
                                (button as ImageButton).image = Drawable.createFromPath(url)
                            }
                        }
                    }
                }
                val titleEditView = contentView.findViewById<EditText>(R.id.edit_painting_title).apply {
                    launch(UI) {
                        setText(model.title)
                    }
                }
                val oldToWipView = contentView.findViewById<CheckBox>(R.id.edit_painting_old_to_wip)
                val datePicker = contentView.findViewById<DatePicker>(R.id.edit_painting_finished_date).apply {
                    launch(UI) {
                        if (model.finished) {
                            visibility = View.VISIBLE
                            updateDate(model.finishingDate!!.year, model.finishingDate!!.monthValue - 1, //datePicker
                                    // behaves strange, and increases the visible moth by 1
                                    model.finishingDate!!.dayOfMonth)

                        } else {
                            visibility = View.GONE
                        }
                    }
                }
                val finishedToggle = contentView.findViewById<ToggleButton>(R.id.edit_painting_finished_toggle).apply {
                    launch(UI) {
                        isChecked = model.finished
                        setOnCheckedChangeListener { _, isChecked ->
                            datePicker.visibility = if (isChecked) View.VISIBLE else View.GONE
                        }
                    }
                }
                setView(contentView)
                setCancelable(true)
                setPositiveButton(R.string.edit_painting_apply) { dialog, _ ->
                    launch(UI) {
                        val newTitle = titleEditView.text.toString()
                        val isFinished = finishedToggle.isChecked
                        val finishedDate = LocalDate.of(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth)
                        //datePicker behaves strangely and decreases the picked month by 1
                        val successTitle: Deferred<Boolean>
                        val successImage: Deferred<Boolean>
                        val successFinished: Deferred<Boolean>
                        successTitle = if (newTitle != model.title) {
                            async(CommonPool) {
                                try {
                                    paintingService.changePainting(model.painting.copy(title = newTitle))
                                    true
                                } catch (e: ServiceException) {
                                    errorDialog(R.string.edit_painting_error_changing_title, e)
                                    false
                                }
                            }
                        } else async(CommonPool) { true }
                        successImage = if (imageDirty) {
                            val oldToWip = oldToWipView.isChecked
                            async(CommonPool) {
                                val inputStream = getInputStreamFromUrl(url)
                                if (inputStream != null) {
                                    try {
                                        paintingService.replaceMainPicture(painting = model.painting,
                                                newPicture = inputStream, moveOldToWip = oldToWip)
                                        true
                                    } catch (e: ServiceException) {
                                        errorDialog(R.string.edit_painting_error_replacing_main_image, e)
                                        false
                                    }
                                } else false
                            }
                        } else async(CommonPool) { true }
                        successFinished = async(CommonPool) {
                            when {
                                isFinished -> {
                                    try {
                                        paintingService.changePainting(model.painting.copy(finished = true,
                                                finishingDate = finishedDate))
                                        true
                                    } catch (e: ServiceException) {
                                        errorDialog(R.string.edit_painting_error_finished, e)
                                        false
                                    }
                                }
                                model.finished -> try {
                                    paintingService.changePainting(
                                            model.painting.copy(finished = false, finishingDate = null))
                                    true
                                } catch (e: ServiceException) {
                                    errorDialog(R.string.edit_painting_error_finished, e)
                                    false
                                }
                                else -> true
                            }
                        }
                        if (successImage.await() && successTitle.await() && successFinished.await()) {
                            dialog.dismiss()
                            loadModel()
                        } else {
                            throw error(
                                    "Failed editing: ${successImage.await()}, ${successTitle.await()}, ${successFinished.await()}")
                        }
                    }
                }
            }.create().show()
        }
        wipGridAdapter = WIPGridAdapter(mutableListOf(), resources, this, paintingService)
        wip.apply {
            val spanCount = Math.floor(
                    displayMetrics.widthPixels/TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 106.toFloat(),
                            displayMetrics).toDouble()).toInt()
            layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            adapter = wipGridAdapter
        }
        add_wip.setOnClickListener {
            pictureProvider.pickPicture {
                if (it != null) {
                    launch(CommonPool) {
                        val stream = getInputStreamFromUrl(it)
                        if (stream != null) {
                            try {
                                paintingService.addWipPicture(model.painting, setOf(stream))
                            } catch (e: ServiceException) {
                                errorDialog(R.string.error_adding_wip, e)
                            }
                            launch(UI) {
                                loadModel()
                            }
                        } else {
                            errorDialog(R.string.error_image_not_readable)
                        }
                    }
                }
            }
        }
        refGridAdapter = RefGridAdapter(mutableListOf(), resources, this, paintingService)
        refs.apply {
            val spanCount = Math.floor(
                    displayMetrics.widthPixels/TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 106.toFloat(),
                            displayMetrics).toDouble()).toInt()
            layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            adapter = refGridAdapter
        }
        add_ref.setOnClickListener {
            pictureProvider.pickPicture {
                if (it != null) {
                    launch(CommonPool) {
                        val stream = getInputStreamFromUrl(it)
                        if (stream != null) {
                            try {
                                paintingService.addReferences(model.painting, setOf(stream))
                            } catch (e: ServiceException) {
                               this@PaintingDetailActivity.errorDialog(R.string.error_adding_ref, e)
                            }
                            launch(UI) {
                                loadModel()
                            }
                        } else {
                            errorDialog(R.string.error_image_not_readable)
                        }
                    }
                }
            }
        }
        sell_painting.setOnClickListener {
            AlertDialog.Builder(this).apply {
                val contentView = layoutInflater.inflate(R.layout.dialog_sell_painting, null, false)
                val purchaserInput = contentView.findViewById<EditText>(R.id.sell_painting_purchaser)
                val priceInput = contentView.findViewById<EditText>(R.id.sell_painting_price)
                val datePicker = contentView.findViewById<DatePicker>(R.id.sell_painting_datePicker)
                setView(contentView)
                setCancelable(true)
                setPositiveButton(R.string.sell_painting_apply) { dialog, _ ->
                    val purchaser = purchaserInput.text.toString().trim()
                    val price = priceInput.text.toString().toDouble()
                    val date = LocalDate.of(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth)
                    //datePicker behaves strangely and decreases the picked month by 1
                    launch(CommonPool) {
                        try {
                            paintingService.sellPainting(model.painting, Purchaser(name = purchaser), date, price)
                        } catch (e: ServiceException) {
                            errorDialog(R.string.error_selling_painting, e)
                        }
                    }
                    dialog.dismiss()
                    loadModel()
                }
            }.create().show()
        }
        swipeRefreshLayout.onRefresh {
            Log.d(this::class.simpleName, "Start refreshing")
            loadModel()
        }
    }

    /** Load model and update view */
    @Synchronized
    fun loadModel() {
        swipeRefreshLayout?.isRefreshing = true
        Log.d(this::class.simpleName, "Started loading of model")
        launch(CommonPool) {
            Log.d("${this@PaintingDetailActivity::class.simpleName}::loadModel", "1 Get Painting")
            val painting = paintingService.get(id)
            Log.d("${this@PaintingDetailActivity::class.simpleName}::loadModel", "2 Assemble model")
            model = DetailModel(mainImage = async(CommonPool) {
                try {
                    designService.getOrLoadFullSizeBitmap(painting.mainPicture)
                } catch (e: ServiceException) {
                    this@PaintingDetailActivity.errorDialog(R.string.error_image_load, e)
                    null
                }
            }, painting = painting,
                    title = painting.title,
                    tags = painting.tags,
                    finished = painting.finished,
                    finishingDate = painting.finishingDate,
                    sold = painting.sellingInfo != null,
                    purchaser = painting.sellingInfo?.purchaser?.name,
                    sellingDate = painting.sellingInfo?.date,
                    price = painting.sellingInfo?.price,
                    wip = async(CommonPool) {
                        painting.wip.map {
                            ImageModel(id = it.id, painting = painting,
                                    image = async(CommonPool) {
                                        try {
                                            designService.getOrLoadThumbnailBitmap(it)
                                        } catch (e: ServiceException) {
                                            this@PaintingDetailActivity.errorDialog(R.string.error_image_load,
                                                    e)
                                            null
                                        }
                                    })
                        }
                    },
                    ref = async(CommonPool) {
                        painting.references.map {
                            ImageModel(id = it.id, painting = painting,
                                    image = async(CommonPool) {
                                        try {
                                            designService.getOrLoadThumbnailBitmap(it)
                                        } catch (e: ServiceException) {
                                            this@PaintingDetailActivity.errorDialog(R.string.error_image_load,
                                                    e)
                                            null
                                        }
                                    })
                        }
                    })
            Log.d("${this@PaintingDetailActivity::class.simpleName}::loadModel", "3 Successfully assembled model")
            Log.d("${this@PaintingDetailActivity::class.simpleName}::loadModel", "4 Updating view")
            async(UI) {
                toolbar_layout.title = model.title
                finishing.text = if (model.finished) getString(R.string.painting_detail_finished, model
                        .finishingDate)
                else getString(R.string.painting_detail_unfinished)
                selling.text = if (model.sold) getString(R.string.painting_detail_sold, model.purchaser,
                        model.sellingDate, model.price)
                else getString(R.string.painting_detail_unsold)
                tagLayout.removeAllViews()
                model.tags.forEach { tag ->
                    tagLayout.addView(
                            (layoutInflater.inflate(R.layout.tag, tagLayout, false) as LinearLayout).apply {
                                findViewById<Button>(R.id.btn)!!.apply button@ {
                                    text = tag
                                    onLongClick {
                                        launch(CommonPool) {
                                            try {
                                                paintingService.removeTags(model.painting,
                                                        setOf(this@button.text.toString()
                                                                .trim()))
                                            } catch (e: ServiceException) {
                                                errorDialog(R.string.error_removing_tag, e)
                                            }
                                            launch(UI) {
                                                loadModel()
                                            }
                                        }
                                    }
                                    onClick {
                                        TODO() //TRACKED: Open search for tag #8
                                    }
                                }
                            })
                }
                tagLayout.addView((layoutInflater.inflate(R.layout.tag, tagLayout, false) as ViewGroup).apply {
                    this.findViewById<Button>(R.id.btn)!!.apply {
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_white_24dp, 0, 0, 0)
                        text = ""
                        onClick {
                            AlertDialog.Builder(this@PaintingDetailActivity).apply {
                                setTitle(R.string.add_tag_title)
                                val contentView = layoutInflater.inflate(R.layout.dialog_add_tag,
                                        null) as LinearLayout
                                val inputView = contentView.findViewById<EditText>(R.id.add_tag_input)
                                setView(contentView)
                                setCancelable(true)
                                setPositiveButton(R.string.add_tag_positive_button, { dialog, _ ->
                                    val input = inputView.text.toString().trim()
                                    if (input.isNotEmpty()) {
                                        launch(CommonPool) {
                                            try {
                                                paintingService.addTags(model.painting, setOf(input))
                                            } catch (e: ServiceException) {
                                                errorDialog(R.string.error_adding_tag, e)
                                            }
                                            launch(UI) {
                                                dialog.dismiss()
                                                loadModel()
                                            }
                                        }
                                    } else dialog.dismiss()
                                })
                            }.create().show()
                        }
                    }

                })
            }.await()
            Log.d("${this@PaintingDetailActivity::class.simpleName}::loadModel",
                    "5 Successfully updated non wait views")
            val mainImageBitmap = model.mainImage.await()
            async(UI) {
                mainImage.apply {
                    if (mainImageBitmap != null) {
                        image = BitmapDrawable(resources, mainImageBitmap)
                        minimumHeight = Math.floor(
                                (mainImage.width.toDouble()/mainImageBitmap.width)*mainImageBitmap.height).toInt()
                        parent.requestLayout()
                    }
                }
            }.await()
            Log.d("${this@PaintingDetailActivity::class.simpleName}::loadModel", "6 Successfully updated mainImage")
            for (i in 1..10) {
                try {
                    wipGridAdapter.models.apply {
                        clear()
                        addAll(model.wip.await())
                        launch(UI) {
                            wipGridAdapter.notifyDataSetChanged()
                        }
                    }
                    break
                } catch (e: UninitializedPropertyAccessException) {
                    Log.w("${this@PaintingDetailActivity::class.simpleName}::loadModel", "Tried to get " +
                            "uninitialized wipGridAdapter $i times")
                    Thread.sleep(100)
                    continue
                }
            }
            Log.d("${this@PaintingDetailActivity::class.simpleName}::loadModel", "7 Successfully updated wip")
            for (i in 1..10) {
                try {
                    refGridAdapter.models.apply {
                        clear()
                        addAll(model.ref.await())
                        launch(UI) {
                            refGridAdapter.notifyDataSetChanged()
                        }
                    }
                    break
                } catch (e: UninitializedPropertyAccessException) {
                    Log.w("${this@PaintingDetailActivity::class.simpleName}::loadModel", "Tried to get " +
                            "uninitialized refGridAdapter $i times")
                    Thread.sleep(100)
                    continue
                }
            }
            Log.d("${this@PaintingDetailActivity::class.simpleName}::loadModel", "8 Successfully updated ref")
            Log.d("${this@PaintingDetailActivity::class.simpleName}::loadModel", "9 Successfully updated views")

            launch(UI) {
                swipeRefreshLayout?.isRefreshing = false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        loadModel()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}

private class DetailModel(var painting: SavedPainting,
                          var mainImage: Deferred<Bitmap?>,
                          var title: String,
                          var tags: Set<String>,
                          var finished: Boolean,
                          var finishingDate: LocalDate?,
                          var sold: Boolean,
                          var purchaser: String?,
                          var sellingDate: LocalDate?,
                          var price: Double?,
                          var wip: Deferred<List<ImageModel>>,
                          var ref: Deferred<List<ImageModel>>)

private data class ImageModel(val image: Deferred<Bitmap?>,
                              val id: String,
                              val painting: SavedPainting)

private abstract class PictureGridAdapter(val models: MutableList<ImageModel>,
                                          val res: Resources,
                                          val context: PaintingDetailActivity): RecyclerView.Adapter<PictureGridAdapter
.ModelViewHolder>
() {

    abstract protected val aysRes: Int
    abstract protected val deleteAction: suspend (id: String, painting: SavedPainting) -> Unit

    /** See [RecyclerView.Adapter.onBindViewHolder] */
    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        val model = models[position]
        launch(UI) {
            val bitmap = model.image.await()
            holder.imageView.image = BitmapDrawable(res, bitmap)
            holder.imageView.onLongClick {
                AlertDialog.Builder(this@PictureGridAdapter.context).apply {
                    setTitle(aysRes)
                    setCancelable(true)
                    setPositiveButton(R.string.delete_wip_ref_delete) { dialog, _ ->
                        launch(UI) {
                            with(models[position]) {
                                deleteAction.invoke(id, painting)
                            }
                            dialog.dismiss()
                            this@PictureGridAdapter.context.loadModel()
                        }
                    }
                    setNegativeButton(R.string.delete_wip_ref_cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                }.create().show()
            }
        }
    }

    /** See [RecyclerView.Adapter.onCreateViewHolder] */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder = ModelViewHolder(
            parent.context.layoutInflater.inflate(R.layout.preview_image_view, parent, false) as ImageView)

    /** See [RecyclerView.Adapter.getItemCount] */
    override fun getItemCount(): Int = models.size

    private class ModelViewHolder(var imageView: ImageView): RecyclerView.ViewHolder(imageView)
}

private class WIPGridAdapter(models: MutableList<ImageModel>,
                             res: Resources,
                             context: PaintingDetailActivity,
                             paintingService: PaintingService)
    : PictureGridAdapter(models, res, context) {
    override val aysRes: Int = R.string.delete_wip_ays
    override val deleteAction: suspend (id: String, painting: SavedPainting) -> Unit = { id, painting ->
        try {
            paintingService.removeWipPicture(painting, setOf(id))
        } catch (e: ServiceException) {
            context.errorDialog(R.string.error_removing_wip, e)
        }
    }
}

private class RefGridAdapter(models: MutableList<ImageModel>,
                             res: Resources,
                             context: PaintingDetailActivity,
                             paintingService: PaintingService)
    : PictureGridAdapter(models, res, context) {
    override val aysRes: Int = R.string.delete_ref_ays
    override val deleteAction: suspend (id: String, painting: SavedPainting) -> Unit = { id, painting ->
        try {
            paintingService.removeReferences(painting, setOf(id))
        } catch (e: ServiceException) {
            context.errorDialog(R.string.error_removing_ref, e)
        }
    }
}