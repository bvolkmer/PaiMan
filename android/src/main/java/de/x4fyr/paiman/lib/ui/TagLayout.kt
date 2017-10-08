package de.x4fyr.paiman.lib.ui

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager


/**
 * Layout that warps at the and of the line
 */
class TagLayout: ViewGroup {

    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int): super(context, attributeSet,
            defStyleAttr)

    private val deviceWidth: Int = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            .let {
                val deviceDisplay = Point()
                it.getSize(deviceDisplay)
                deviceDisplay.x
            }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        var curLeft: Int
        var curTop: Int
        var maxHeight: Int

        //get the available size of child view
        val childLeft = this.paddingLeft
        val childTop = this.paddingTop
        val childRight = this.measuredWidth - this.paddingRight
        val childBottom = this.measuredHeight - this.paddingBottom
        val childWidth = childRight - childLeft
        val childHeight = childBottom - childTop

        maxHeight = 0
        curLeft = childLeft
        curTop = childTop
        if (count == 0) return
        for (i: Int in 0 until count) {
            var curWidth: Int
            var curHeight: Int
            val child = getChildAt(i)
            if (child.visibility == GONE)
                continue

            //Get the maximum size of the child
            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST))
            curWidth = child.measuredWidth
            curHeight = child.measuredHeight
            //wrap is reach to the end
            if (curLeft + curWidth >= childRight) {
                curLeft = childLeft
                curTop += maxHeight
                maxHeight = 0
            }
            //do the layout
            child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight)
            //store the max height
            if (maxHeight < curHeight)
                maxHeight = curHeight
            curLeft += curWidth
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val count = childCount
        // Measurement will ultimately be computing these values.
        var maxHeight = 0
        var maxWidth = 0
        var childState = 0
        var mLeftWidth = 0
        var rowCount = 0
        // Iterate through all children, measuring them and computing our dimensions
        // from their size.
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE)
                continue
            // Measure the child.
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            maxWidth += Math.max(maxWidth, child.measuredWidth)
            mLeftWidth += child.measuredWidth
            if (mLeftWidth/deviceWidth > rowCount) {
                maxHeight += child.measuredHeight
                rowCount++
            } else {
                maxHeight = Math.max(maxHeight, child.measuredHeight)
            }
            childState = View.combineMeasuredStates(childState, child.measuredState)
        }
        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, suggestedMinimumHeight)
        maxWidth = Math.max(maxWidth, suggestedMinimumWidth)
        // Report our final dimensions.
        setMeasuredDimension(View.resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                View.resolveSizeAndState(maxHeight, heightMeasureSpec, childState shl View.MEASURED_HEIGHT_STATE_SHIFT))
    }
}