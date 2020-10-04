package reyst.gsihome

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NumberVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val textView: TextView = itemView.findViewById(R.id.tv_number)
    fun bind(number: Int) {
        textView.text = number.toString()
    }
}