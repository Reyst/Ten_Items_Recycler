package reyst.gsihome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class NumberAdapter : RecyclerView.Adapter<NumberVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return NumberVH(view)
    }

    override fun onBindViewHolder(holder: NumberVH, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 10

}