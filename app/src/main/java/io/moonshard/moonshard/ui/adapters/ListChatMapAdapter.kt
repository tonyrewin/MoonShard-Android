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
import io.moonshard.moonshard.models.api.RoomPin


interface ListChatMapListener {
    fun clickChat(categoryName: String)
}

class ListChatMapAdapter (val listener: ListChatMapListener, private var chats: List<RoomPin>) :
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
        holder.groupIv?.setImageResource(R.drawable.ic_heart)
        holder.groupNameTv?.text = "test"
        holder.valueMembersTv?.text = "test"
        holder.locationValueTv?.text = "test"


        holder.itemView.setOnClickListener {
            focusedItem = position
            notifyDataSetChanged()
            listener.clickChat("test")
        }
    }

    override fun getItemCount(): Int = chats.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var groupNameTv: TextView? = view.findViewById(R.id.groupNameTv)
        internal var valueMembersTv: TextView? = view.findViewById(R.id.valueMembersTv)
        internal var locationValueTv: TextView? = view.findViewById(R.id.locationValueTv)
        internal var groupIv: ImageView? = view.findViewById(R.id.profile_image)
        internal var mainLayout: RelativeLayout? = view.findViewById(R.id.mainLayout)
    }
}