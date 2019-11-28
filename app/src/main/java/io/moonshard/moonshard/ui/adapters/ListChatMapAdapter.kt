package io.moonshard.moonshard.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.Category


interface ListChatMapListener {
    fun clickChat(categoryName: String)
}

class ListChatMapAdapter (val listener: ListChatMapListener, private var chats: List<Category>) :
    RecyclerView.Adapter<ListChatMapAdapter.ViewHolder>() {

    var focusedItem = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.chat_map_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.groupIv?.setImageResource(chats[position].drawable)
        holder.groupNameTv?.text = chats[position].name
        holder.valueMembersTv?.text = chats[position].name
        holder.locationValueTv?.text = chats[position].name


        holder.itemView.setOnClickListener {
            focusedItem = position
            notifyDataSetChanged()
            listener.clickChat(chats[position].name)
        }
    }

    override fun getItemCount(): Int = chats.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var groupNameTv: TextView? = view.findViewById(R.id.groupNameTv)
        internal var valueMembersTv: TextView? = view.findViewById(R.id.valueMembersTv)
        internal var locationValueTv: TextView? = view.findViewById(R.id.locationValueTv)
        internal var groupIv: ImageView? = view.findViewById(R.id.profileImage)
        internal var mainLayout: RelativeLayout? = view.findViewById(R.id.mainLayout)
    }
}