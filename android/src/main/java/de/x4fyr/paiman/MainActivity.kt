package de.x4fyr.paiman

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View.INVISIBLE
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.coroutines.experimental.*
import org.jetbrains.anko.support.v4.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        progressBar.apply {
            progress = 0
            secondaryProgress = 0
            max = 10
        }
        load({ progressBar.progress = it }, { progressBar.visibility = INVISIBLE })
        swipeRefreshLayout.onRefresh {
            load(completionHandler = { swipeRefreshLayout.isRefreshing = false })
        }
    }


    private fun load(onProgress: (Int) -> Unit = {}, completionHandler: () -> Unit = {}) {
        doAsync {
            bg {
                for (i in 1..10) {
                    Thread.sleep(500) //TODO Load
                    uiThread {
                        onProgress(i * 10)
                    }
                }
            }.invokeOnCompletion {
                uiThread {
                    completionHandler()
                }
            }
        }
    }
}

private data class CardModel(val title: String, val picture: Drawable)

private class ListAdapter: RecyclerView.Adapter<ListAdapter.Holder>() {

    override fun getItemCount(): Int {
        TODO("not implemented") //TODO: not implemented
    }

    override fun onBindViewHolder(holder: Holder?, position: Int) {
        TODO("not implemented") //TODO: not implemented
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
        TODO("not implemented") //TODO: not implemented
    }

    class Holder(view: CardView): RecyclerView.ViewHolder(view) {
    }

}