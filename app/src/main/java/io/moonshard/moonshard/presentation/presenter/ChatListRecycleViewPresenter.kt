package io.moonshard.moonshard.presentation.presenter

import android.view.View
import io.moonshard.moonshard.common.utils.DateHolder
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.ChatListRecyclerView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.repository.MessageRepository
import io.moonshard.moonshard.ui.adapters.ChatListAdapter
import io.reactivex.disposables.Disposable
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jxmpp.jid.impl.JidCreate
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

@InjectViewState
class ChatListRecycleViewPresenter: MvpPresenter<ChatListRecyclerView>() {
    private var chats = emptyList<ChatEntity>()
    private val chatListRepository = ChatListRepository()
    private val messageRepository = MessageRepository()

    private val disposables = emptyList<Disposable>().toMutableList()

    init {
        disposables.add(chatListRepository.getChats().subscribe {
            chats = it
        })
    }

    fun getChatListSize(): Int {
        return chats.size
    }

    fun onBindViewHolder(holder: ChatListAdapter.ChatListViewHolder, position: Int) {
        val chat = chats[position]
        holder.chatName.text = chat.chatName
        disposables.add(messageRepository.getUnreadMessagesCountByJid(JidCreate.bareFrom(chat.jid)).subscribe {
            if (it > 0) {
                holder.unreadMessageCount.text = it.toString()
            } else {
                holder.unreadMessageCount.visibility = View.INVISIBLE
            }
        })
        disposables.add(messageRepository.getLastMessage(JidCreate.bareFrom(chat.jid)).subscribe({ message ->
            holder.lastMessageText.visibility = View.VISIBLE
            holder.lastMessageDate.visibility = View.VISIBLE
            holder.lastMessageReadState.visibility = View.VISIBLE

            holder.lastMessageText.text = message.text

            val currentDate = DateHolder(System.currentTimeMillis())
            val messageDate = DateHolder(message.timestamp)
            val lastMessageDateText = if (messageDate.year == currentDate.year && messageDate.month == currentDate.month
                && messageDate.dayOfMonth == currentDate.dayOfMonth) {
                String.format("%d:%d", messageDate.hour, messageDate.minute)
            } else if (messageDate.year == currentDate.year && messageDate.month == currentDate.month
                && messageDate.weekOfMonth == currentDate.weekOfMonth) {
                String.format("%ta", messageDate.calendar)
            } else if ((messageDate.year == currentDate.year && messageDate.month == currentDate.month)
                || messageDate.year == currentDate.year) {
                String.format("%d %tB", messageDate.dayOfMonth, messageDate.calendar)
            } else {
                String.format("%d.%d.%d", messageDate.dayOfMonth, messageDate.month, messageDate.year)
            }

            holder.lastMessageDate.text = lastMessageDateText
        }, {
            holder.lastMessageText.visibility = View.INVISIBLE
            holder.lastMessageDate.visibility = View.INVISIBLE
            holder.lastMessageReadState.visibility = View.INVISIBLE
        }))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.forEach {
            it.dispose()
        }
    }
}