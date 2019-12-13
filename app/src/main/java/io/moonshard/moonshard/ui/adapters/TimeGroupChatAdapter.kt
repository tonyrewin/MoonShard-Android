package io.moonshard.moonshard.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R


interface RvTimeListener {
    fun clickChat(time: String)
}

class TimeGroupChatAdapter(
    val listener: RvTimeListener, private var times: List<String>
) :
    RecyclerView.Adapter<TimeGroupChatAdapter.ViewHolder>() {

    var focusedItem = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.time_item, parent, false)
        )

    override fun getItemCount(): Int = times.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.time?.text = times[position]

        if (focusedItem == position) {
            holder.checkIv?.visibility = View.VISIBLE
            holder.mainLayout?.setBackgroundColor(Color.parseColor("#EEF6FF"))
        } else {
            holder.checkIv?.visibility = View.GONE
            holder.mainLayout?.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
        holder.itemView.setOnClickListener {
            focusedItem = position
            notifyDataSetChanged()
            listener.clickChat(times[position])
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var time: TextView? = view.findViewById(R.id.dateTv)
        internal val checkIv: ImageView? = view.findViewById(R.id.checkIv)
        internal val lineView: View? = view.findViewById(R.id.lineView)
        internal val mainLayout: RelativeLayout? = view.findViewById(R.id.mainLayout)
    }

}