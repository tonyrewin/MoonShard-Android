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
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.presenter.ChatListRecycleViewPresenter
import io.moonshard.moonshard.presentation.view.ChatListRecyclerView
import kotlinx.android.synthetic.main.chat_item.view.*
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter


interface ChatListListener {
    fun clickChat(chat: ChatEntity)
}

class ChatListAdapter(parentDelegate: MvpDelegate<*>, private val listener: ChatListListener) :
    MvpBaseAdapter<ChatListAdapter.ChatListViewHolder>(parentDelegate, 0.toString()),
    ChatListRecyclerView {

    init {
        setHasStableIds(true)
    }

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

    override fun getItemId(position: Int): Long {
        return presenter.getItemId(position)
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
            presenter.deleteChatOnUI(viewHolder.adapterPosition)
            showSnackbar(MainApplication.getMainActivity().getString(R.string.chatlist_chat_deleted))
        }
    }

    fun showSnackbar(text: String) {
        val snackbar = Snackbar.make(
            MainApplication.getMainActivity().findViewById(android.R.id.content),
            text, Snackbar.LENGTH_SHORT
        )
        snackbar.setAction(R.string.snackbar_undo) { v ->
            v.setOnClickListener {
                presenter.bringChatBack()
            }
        }
        snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                if (event == DISMISS_EVENT_TIMEOUT) {
                    presenter.deleteChatFinally()
                }
            }
        })
        snackbar.show()
    }

    inner class ChatListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var avatar: ImageView = view.profileImage
        var chatName: TextView = view.chatItemName
        var lastMessageText: TextView = view.lastMessageChatText
        var lastMessageReadState: ImageView = view.lastMessageReadState
        var lastMessageDate: TextView = view.lastMessageDate
        var unreadMessageCount: TextView = view.unreadMessageCount
    }
}