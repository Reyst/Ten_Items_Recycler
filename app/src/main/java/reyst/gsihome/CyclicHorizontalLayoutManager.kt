package reyst.gsihome

import android.util.Log
import android.util.SparseArray
import android.view.View
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView

class CyclicHorizontalLayoutManager : RecyclerView.LayoutManager() {

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
        fillDown(recycler)
    }

    private fun fillDown(recycler: RecyclerView.Recycler) {

        var pos = 0
        var fillDown = true
        var viewStart = 0

        while (fillDown && pos < itemCount) {
            val view = recycler.getViewForPosition(pos)
            addView(view)
            measureChildWithMargins(view, 0, 0)

            layoutDecorated(
                view,
                viewStart + getDecoratedLeft(view) + view.marginStart,
                getDecoratedTop(view) + view.marginTop,
                viewStart + getDecoratedMeasuredWidth(view) + view.marginEnd,
                getDecoratedMeasuredHeight(view) //+ view.marginBottom
            )

            viewStart = getDecoratedRight(view) + view.marginEnd
            fillDown = viewStart <= width
            pos++
        }
    }

}