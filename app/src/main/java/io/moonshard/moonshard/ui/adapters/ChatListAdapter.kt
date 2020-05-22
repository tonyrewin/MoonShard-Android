package io.moonshard.moonshard.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.ChatListItem
import io.moonshard.moonshard.presentation.presenter.ChatListRecycleViewPresenter
import io.moonshard.moonshard.presentation.view.ChatListRecyclerView
import kotlinx.android.synthetic.main.chat_item.view.*
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter
import org.jxmpp.jid.Jid


interface ChatListListener {
    fun clickChat(chat: ChatListItem)
}

class ChatListAdapter(parentDelegate: MvpDelegate<*>, private val listener: ChatListListener) :
    MvpBaseAdapter<ChatListAdapter.ChatListViewHolder>(parentDelegate, 0.toString()),
    ChatListRecyclerView {

    @InjectPresenter
    lateinit var presenter: ChatListRecycleViewPresenter

    private val recentlyDeletedChats = mutableMapOf<Jid, ChatListItem>()

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

    override fun setData(chats: List<ChatListItem>) {
        presenter.setData(chats)
    }

    override fun onDataChange() {
        MainApplication.getMainUIThread().post {
            notifyDataSetChanged()
        }
    }

    override fun onItemChange(position: Int) {
        MainApplication.getMainUIThread().post {
            notifyItemChanged(position)
        }
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        presenter.onBindViewHolder(holder, position, listener)

    }

    override fun onItemDelete(position: Int) {
        MainApplication.getMainUIThread().post {
            notifyItemRemoved(position)
        }
    }

    inner class SwipeToDeleteCallback : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val chat = presenter.deleteChatOnUI(viewHolder.adapterPosition)
            recentlyDeletedChats[chat.jid] = chat
            showSnackBar(MainApplication.getMainActivity().getString(R.string.chatlist_chat_deleted), chat.jid)
        }
    }

    fun showSnackBar(text: String, jid: Jid) {
        val snackBar = Snackbar.make(
            MainApplication.getMainActivity().findViewById(android.R.id.content),
            text, Snackbar.LENGTH_SHORT
        )
        snackBar.setAction(R.string.snackbar_undo) { v ->
            presenter.bringChatBack(jid)
            recentlyDeletedChats.remove(jid)
        }
        snackBar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                if (event == DISMISS_EVENT_TIMEOUT) {
                    presenter.deleteChatFinally(jid)
                    recentlyDeletedChats.remove(jid)
                }
            }
        })
        snackBar.show()
    }

    inner class ChatListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var avatar: ImageView = view.profileImage
        var chatName: TextView = view.chatItemName
        var lastMessageText: TextView = view.lastMessageChatText
        var lastMessageReadState: ImageView = view.lastMessageReadState
        var lastMessageDate: TextView = view.lastMessageDate
        var unreadMessageCount: TextView = view.unreadMessageCount
        var viewLine:View? = view.viewLine
    }
}