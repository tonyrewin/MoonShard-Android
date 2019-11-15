package io.moonshard.moonshard.ui.adapters

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.moonshard.moonshard.models.GenericDialog
import java.util.*


interface RvListener {
    fun clickChat(idChat: String)
}

class MyChatsAdapter(val listener: RvListener, private var chats: List<GenericDialog>) :
    RecyclerView.Adapter<MyChatsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                io.moonshard.moonshard.R.layout.chat_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get()
            .load("https://dok7xy59qfw9h.cloudfront.net/479/254/442/-329996983-20arpee-1ersd6l4segk3e5/original/file.jpg")
            .into(holder.avatar)

        holder.itemView.setOnClickListener {
            listener.clickChat(chats[position].id)
        }
    }

    fun setItems(items: List<GenericDialog>) {
        this.chats = items
        notifyDataSetChanged()
    }

    fun sort(comparator: Comparator<GenericDialog>) {
        Collections.sort<GenericDialog>(chats, comparator)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = chats.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var avatar: ImageView? =
            view.findViewById(io.moonshard.moonshard.R.id.profile_image)
    }
}