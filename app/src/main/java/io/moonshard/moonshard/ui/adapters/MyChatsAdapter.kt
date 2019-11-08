package io.moonshard.moonshard.ui.adapters

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


interface RvListener {
    fun clickChat(driver: String)
}

class MyChatsAdapter(val listener: RvListener, private val chats: List<String>) :
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
        //   holder.avatar?.setLayerType(View.LAYER_TYPE_HARDWARE, null)


        if (position == 14) {

        }

    }

    override fun getItemCount(): Int = 15

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var avatar: ImageView? =
            view.findViewById(io.moonshard.moonshard.R.id.profile_image)
    }
}