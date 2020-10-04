package reyst.gsihome

import android.util.Log
import android.util.SparseArray
import android.view.View
import androidx.core.util.forEach
import androidx.recyclerview.widget.RecyclerView

class CyclicHorizontalLayoutManager : RecyclerView.LayoutManager() {

    private val viewCache = SparseArray<View>()

    override fun canScrollHorizontally(): Boolean = true
    override fun canScrollVertically(): Boolean = false

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    override fun isAutoMeasureEnabled(): Boolean = true

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        Log.wtf("INSPECT", "onLayoutChildren state: $state")
        detachAndScrapAttachedViews(recycler)
        initCache()
        fillDown(recycler)
        recycleCache(recycler)
    }

    private fun initCache() {
        viewCache.clear()
        (0 until childCount)
            .mapNotNull { getChildAt(it) }
            .forEach { viewCache.put(getPosition(it), it) }

        viewCache.forEach { _, view -> detachView(view) }
    }

    private fun recycleCache(recycler: RecyclerView.Recycler) {
        viewCache.forEach { _, view -> recycler.recycleView(view) }
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        offsetChildrenHorizontal(-dx)
        return dx
    }

    private fun fillDown(recycler: RecyclerView.Recycler) {

        var pos = 0
        var fillDown = true
        var viewStart = 0

        while (fillDown) { //  && pos < itemCount

            val view = viewCache[pos] ?: recycler.getViewForPosition(pos % itemCount)

            if (viewCache[pos] == null) {
                addView(view)
                measureChildWithMargins(view, 0, 0)

                layoutDecorated(
                    view,
                    viewStart, // + getDecoratedLeft(view),
                    getDecoratedTop(view),
                    viewStart + getDecoratedMeasuredWidth(view),
                    getDecoratedMeasuredHeight(view)
                )
            } else {
                attachView(view)
                viewCache.remove(pos)
            }

            viewStart = getDecoratedRight(view)
            fillDown = viewStart <= width
            pos++
        }
    }

}