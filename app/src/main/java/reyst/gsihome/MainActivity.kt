package reyst.gsihome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val adapter = NumberAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv = findViewById<RecyclerView>(R.id.rv)
        rv.layoutManager = CyclicHorizontalLayoutManager()
        rv.adapter = adapter
            //LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
    }
}

