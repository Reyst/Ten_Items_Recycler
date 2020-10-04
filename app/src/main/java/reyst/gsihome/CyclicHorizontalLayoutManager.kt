package reyst.gsihome

import android.graphics.Rect
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
        detachAndScrapAttachedViews(recycler)
        fill(recycler, state)
    }

    private fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        Log.wtf("INSPECT", "state: $state")

        val anchorView = getAnchorView()

        fillCache()
        fillToStart(anchorView, recycler)
        fillToEnd(anchorView, recycler)
        recycleCache(recycler)
    }

    private fun getAnchorView(): View? {
        val recycleRect = Rect(0, 0, width, height)

        return (0 until childCount)
            .mapNotNull { getChildAt(it) }
            .mapNotNull { view ->
                getIntersectSquare(recycleRect, view)
                    ?.let { square -> square to view }
            }
            .toSortedSet { pair1, pair2 -> -pair1.first.compareTo(pair2.first) }
            .firstOrNull()
            ?.second
    }

    private fun getIntersectSquare(recycleRect: Rect, view: View): Int? {
        return Rect(
            getDecoratedLeft(view),
            getDecoratedTop(view),
            getDecoratedRight(view),
            getDecoratedBottom(view),
        ).takeIf { it.intersect(recycleRect) }
            ?.let { it.width() * it.height() }
    }

    private fun fillCache() {
        viewCache.clear()
        (0 until childCount)
            .mapNotNull { getChildAt(it) }
            .forEach { viewCache.put(getPosition(it), it) }

        viewCache.forEach { _, view -> detachView(view) }

        Log.wtf("INSPECT", "Fill - viewCache size: ${viewCache.size()}")
    }

    private fun recycleCache(recycler: RecyclerView.Recycler) {
        Log.wtf("INSPECT", "Recycle - viewCache size: ${viewCache.size()}")
        viewCache.forEach { _, view -> recycler.recycleView(view) }
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        offsetChildrenHorizontal(-dx)
        fill(recycler, state)
        return dx
    }

    private fun fillToStart(anchorView: View?, recycler: RecyclerView.Recycler) {
        var pos = anchorView?.let { getPosition(it) - 1 } ?: (itemCount - 1)
        var viewEnd = anchorView?.let { getDecoratedLeft(it) } ?: 0

        var fill = true

        while (fill) { //  && pos < itemCount

            val view = viewCache[pos]
                ?: pos.let {
                    val intermediateIndex = (pos % itemCount)
                    val index = if (intermediateIndex < 0) itemCount + intermediateIndex else intermediateIndex
                    recycler.getViewForPosition(index)
                }

            if (viewCache[pos] == null) {
                addView(view, 0)
                measureChildWithMargins(view, 0, 0)

                layoutDecorated(
                    view,
                    viewEnd - getDecoratedMeasuredWidth(view), // + getDecoratedLeft(view),
                    getDecoratedTop(view),
                    viewEnd,
                    getDecoratedMeasuredHeight(view)
                )
            } else {
                attachView(view)
                viewCache.remove(pos)
            }

            viewEnd = getDecoratedLeft(view)
            fill = viewEnd > 0
            pos--
        }
    }

    private fun fillToEnd(anchorView: View?, recycler: RecyclerView.Recycler) {

        var pos = anchorView?.let { getPosition(it) } ?: 0
        var viewStart = anchorView?.let { getDecoratedLeft(it) } ?: 0

        var fillDown = true

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