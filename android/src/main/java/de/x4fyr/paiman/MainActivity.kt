package de.x4fyr.paiman

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import de.x4fyr.paiman.app.ApplyingDialogFragment
import de.x4fyr.paiman.lib.provider.AndroidPictureProvider
import de.x4fyr.paiman.lib.provider.AndroidServiceProvider
import de.x4fyr.paiman.lib.services.DesignService
import de.x4fyr.paiman.lib.services.PaintingService
import de.x4fyr.paiman.lib.services.QueryService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.uiThread

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        serviceProvider = AndroidServiceProvider(this)
        pictureProvider = AndroidPictureProvider(this)
        queryService = serviceProvider.queryService
        paintingService = serviceProvider.paintingService
        designService = serviceProvider.designService
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
        progressBar.apply {
            progress = 0
            secondaryProgress = 0
            max = 10
        }
        loadContent({ progressBar.progress = it }, { progressBar.visibility = INVISIBLE })
        swipeRefreshLayout.onRefresh {
            loadContent(completionHandler = { swipeRefreshLayout.isRefreshing = false })
        }
    }


    /**
     *
     */
    private fun loadContent(onProgress: (percent: Int) -> Unit = {}, completionHandler: () -> Unit = {}) {
        doAsync {
            bg {
                queryService.allPaintingsQuery.toLiveQuery().apply {
                    addChangeListener {
                        paintingService.getFromQueryResult(it.rows)
                    }
                    start()
                }
                uiThread { onProgress(50) }
            }.invokeOnCompletion {
                uiThread {
                    completionHandler()
                }
            }
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