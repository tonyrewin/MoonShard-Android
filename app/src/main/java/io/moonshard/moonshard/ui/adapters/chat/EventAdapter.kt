package io.moonshard.moonshard.ui.adapters.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R


interface EventListener {
    fun eventClick(categoryName: String)
}

class EventAdapter(
    val listener: EventListener,
    private var events: List<String>
) :
    RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int = 10


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.event_item,
                parent,
                false
            )
        )

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var nameEvent: TextView? = view.findViewById(R.id.nameEvent)
        internal var descriptionTv: TextView? = view.findViewById(R.id.descriptionTv)
        internal var avatarEvent: ImageView? = view.findViewById(R.id.avatarEvent)
        internal var showGroupBtn: Button? = view.findViewById(R.id.showGroupBtn)
    }
}