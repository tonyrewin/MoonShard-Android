package io.moonshard.moonshard.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.GenericMessage


class MessagesAdapter(private var messages: ArrayList<GenericMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                return ViewHolderMyMessage(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.my_message,
                        parent,
                        false
                    )
                )
            }
            1 -> {
                return ViewHolderDifferentMessage(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.their_message,
                        parent,
                        false
                    )
                )
            }
            else -> {
                return ViewHolderMyMessage(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.my_message,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(messages.get(position).isBelongsToCurrentUser){
            return 0
        }else{
            return 1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            0->{
                (holder as ViewHolderMyMessage).bodyText?.text = messages[position].text
            }
            1->{
                (holder as ViewHolderDifferentMessage).bodyText?.text = messages[position].text
                holder.name?.text = messages[position].user.name
                holder.avatar?.setBackgroundColor(Color.parseColor("#7CFC00"))
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun add(message: GenericMessage) {
        messages.add(message)
        notifyDataSetChanged() // to render the list we need to notify
    }

    override fun getItemCount(): Int = messages.size

    inner class ViewHolderMyMessage(view: View) : RecyclerView.ViewHolder(view) {
        internal var bodyText: TextView? = view.findViewById(R.id.message_body)
    }

    inner class ViewHolderDifferentMessage(view: View) : RecyclerView.ViewHolder(view) {
        internal var avatar: ImageView? = view.findViewById(R.id.avatar)
        internal var name: TextView? = view.findViewById(R.id.name)
        internal var bodyText: TextView? = view.findViewById(R.id.message_body)

    }
}