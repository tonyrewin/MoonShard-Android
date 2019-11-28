package io.moonshard.moonshard.presentation.presenter

import android.view.View
import io.moonshard.moonshard.common.utils.DateHolder
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.ChatListRecyclerView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.repository.MessageRepository
import io.moonshard.moonshard.ui.adapters.ChatListAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jxmpp.jid.impl.JidCreate
import java.util.*

@InjectViewState
class ChatListRecycleViewPresenter: MvpPresenter<ChatListRecyclerView>() {
    private var chats = emptyList<ChatEntity>()
    private val chatListRepository = ChatListRepository()
    private val messageRepository = MessageRepository()

    private val disposables = emptyList<Disposable>().toMutableList()

    init {
        disposables.add(chatListRepository.getChats()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { newChats ->
            chats = newChats
            chats = chats.sortedWith(Comparator { o1, o2 ->
                val timestamp1 = o1.messages.sortedWith(compareByDescending { it.timestamp }).first().timestamp
                val timestamp2 = o2.messages.sortedWith(compareByDescending { it.timestamp }).first().timestamp
                timestamp2.compareTo(timestamp1)
            })
            viewState.onDataChange()
        })
    }

    fun getChatListSize(): Int {
        return chats.size
    }

    fun onBindViewHolder(holder: ChatListAdapter.ChatListViewHolder, position: Int) {
        val chat = chats[position]
        holder.chatName.text = chat.chatName
        disposables.add(messageRepository.getRealUnreadMessagesCountByJid(JidCreate.bareFrom(chat.jid))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
            if (it > 0) {
                holder.unreadMessageCount.text = it.toString()
                viewState.onItemChange(position)
            } else {
                holder.unreadMessageCount.visibility = View.INVISIBLE
                viewState.onItemChange(position)
            }
        })
        disposables.add(messageRepository.getLastMessage(JidCreate.bareFrom(chat.jid))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ message ->
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
            viewState.onItemChange(position)
        }, {
            holder.lastMessageText.visibility = View.INVISIBLE
            holder.lastMessageDate.visibility = View.INVISIBLE
            holder.lastMessageReadState.visibility = View.INVISIBLE
            viewState.onItemChange(position)
        }))
        viewState.onItemChange(position)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.forEach {
            it.dispose()
        }
    }
}