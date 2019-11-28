package io.moonshard.moonshard.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.presenter.ChatListRecycleViewPresenter
import io.moonshard.moonshard.presentation.view.ChatListRecyclerView
import kotlinx.android.synthetic.main.chat_item.view.*
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter


interface ChatListListener {
    fun clickChat(chat: ChatEntity)
}

class ChatListAdapter(parentDelegate: MvpDelegate<*>,val listener: ChatListListener):
    MvpBaseAdapter<ChatListAdapter.ChatListViewHolder>(parentDelegate, 0.toString()), ChatListRecyclerView {

    @InjectPresenter
    lateinit var presenter: ChatListRecycleViewPresenter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        return ChatListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.chat_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return presenter.getChatListSize()
    }

    override fun onDataChange() {
        notifyDataSetChanged()
    }

    override fun onItemChange(position: Int) {
        runOnUiThread{
            notifyItemChanged(position)
        }
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        presenter.onBindViewHolder(holder, position,listener)
    }

    inner class ChatListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var avatar: ImageView = view.profileImage
        var chatName: TextView = view.chatItemName
        var lastMessageText: TextView = view.lastMessageChatText
        var lastMessageReadState: ImageView = view.lastMessageReadState
        var lastMessageDate: TextView = view.lastMessageDate
        var unreadMessageCount: TextView = view.unreadMessageCount
    }
}