package pl.lipov.eventslogger.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_log.view.*
import pl.lipov.eventslogger.R

class LogAdapter(
    private var logs: List<String>
) : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): LogViewHolder = LogViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
    )

    override fun onBindViewHolder(
        holder: LogViewHolder,
        position: Int
    ) {
        holder.itemText.text = logs[position]
    }

    override fun getItemCount(): Int = logs.size

    fun saveLogs(
        logs: List<String>
    ) {
        this.logs = logs
        notifyDataSetChanged()
    }

    class LogViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {
        val itemText: TextView = view.item_text
    }
}
