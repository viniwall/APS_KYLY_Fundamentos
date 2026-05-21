package br.com.kollectaops.collector.presentation.picking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.kollectaops.collector.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class LastReadsAdapter : RecyclerView.Adapter<LastReadsAdapter.ViewHolder>() {

    private val reads = mutableListOf<Pair<String, String>>()

    fun addRead(barcode: String) {
        val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        reads.add(0, Pair(barcode, time))
        if (reads.size > 20) reads.removeAt(reads.size - 1)
        notifyItemInserted(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_last_read, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (barcode, time) = reads[position]
        holder.tvBarcode.text = "✓ $barcode"
        holder.tvTime.text = time
    }

    override fun getItemCount() = reads.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBarcode: TextView = view.findViewById(R.id.tvBarcode)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
    }
}
