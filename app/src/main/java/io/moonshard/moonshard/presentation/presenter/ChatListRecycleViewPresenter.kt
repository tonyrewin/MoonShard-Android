package io.moonshard.moonshard.presentation.presenter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.DateHolder
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.ChatListRecyclerView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.repository.MessageRepository
import io.moonshard.moonshard.ui.adapters.ChatListAdapter
import io.moonshard.moonshard.ui.adapters.ChatListListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import trikita.log.Log
import java.util.*

@InjectViewState
class ChatListRecycleViewPresenter : MvpPresenter<ChatListRecyclerView>() {
    private var chats = emptyList<ChatEntity>().toMutableList()
    private var fullChats = emptyList<ChatEntity>().toMutableList()


    private var recentlyDeletedItem: ChatEntity? = null

     val bindedItems = emptyList<ChatEntity>().toMutableList()
    private val disposables = emptyList<Disposable>().toMutableList()

    init {
        disposables.add(ChatListRepository.getChats()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { newChats ->
                chats = newChats.toMutableList()
                fullChats= newChats.toMutableList()
                sortChatsByRecentlyUpdatedDate()
                viewState.onDataChange()
            })
    }

    fun getChatListSize(): Int {
        return chats.size
    }

    fun getItemId(position: Int): Long {
        return chats[position].id
    }

    fun onBindViewHolder(
        holder: ChatListAdapter.ChatListViewHolder,
        position: Int,
        listener: ChatListListener
    ) {
        val chat = chats[position]
        MainApplication.getXmppConnection().addChatStatusListener(chat.jid)
        if (chat.isGroupChat) {
            joinChat(chat.jid)
        } else {
            MainApplication.getXmppConnection().chatManager.chatWith(JidCreate.entityBareFrom(chat.jid))
        }
            if (bindedItems.indexOf(chat) == -1) {
            setAvatar(chat.jid, chat.chatName, holder.avatar)
            holder.chatName.visibility = View.VISIBLE
            holder.chatName.text = chat.chatName

            disposables.add(MessageRepository.getRealUnreadMessagesCountByJid(
                JidCreate.bareFrom(
                    chat.jid
                )
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe ({
                    if (it > 0) {
                        holder.unreadMessageCount.visibility = View.VISIBLE
                        holder.unreadMessageCount.text = it.toString()
                        viewState.onItemChange(position)
                    } else {
                        holder.unreadMessageCount.visibility = View.INVISIBLE
                        viewState.onItemChange(position)
                    }
                },{
                    com.orhanobut.logger.Logger.d(it)
                }))
            disposables.add(MessageRepository.getLastMessage(JidCreate.bareFrom(chat.jid))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ message ->
                    holder.lastMessageText.visibility = View.VISIBLE
                    holder.lastMessageDate.visibility = View.VISIBLE
                    holder.lastMessageReadState.visibility = View.VISIBLE

                    holder.lastMessageText.text = message.text

                    val currentDate = DateHolder(System.currentTimeMillis())
                    val messageDate = DateHolder(message.timestamp)
                    val lastMessageDateText =
                        if (messageDate.year == currentDate.year && messageDate.month == currentDate.month
                            && messageDate.dayOfMonth == currentDate.dayOfMonth
                        ) {
                            String.format("%d:%d", messageDate.hour, messageDate.minute)
                        } else if (messageDate.year == currentDate.year && messageDate.month == currentDate.month
                            && messageDate.weekOfMonth == currentDate.weekOfMonth
                        ) {
                            String.format("%ta", messageDate.calendar)
                        } else if ((messageDate.year == currentDate.year && messageDate.month == currentDate.month)
                            || messageDate.year == currentDate.year
                        ) {
                            String.format(
                                "%d %tB",
                                messageDate.dayOfMonth,
                                messageDate.calendar
                            )
                        } else {
                            String.format(
                                "%d.%d.%d",
                                messageDate.dayOfMonth,
                                messageDate.month,
                                messageDate.year
                            )
                        }
                    holder.lastMessageReadState.visibility = View.VISIBLE
                    holder.lastMessageReadState.setImageResource(if (message.isSent && message.isRead) R.drawable.ic_checked_message_state else R.drawable.ic_sent_message_state)
                    holder.lastMessageDate.text = lastMessageDateText
                    viewState.onItemChange(position)
                }, {
                    holder.lastMessageText.visibility = View.INVISIBLE
                    holder.lastMessageDate.visibility = View.INVISIBLE
                    holder.lastMessageReadState.visibility = View.INVISIBLE
                    viewState.onItemChange(position)
                })
            )
            viewState.onItemChange(position)

            holder.itemView.setOnClickListener {
                listener.clickChat(chat)
            }
            bindedItems.add(chat)
        }
    }

    private fun setAvatar(jid: String, nameChat: String, imageView: ImageView) {
        if (MainApplication.getCurrentChatActivity() != jid) {
            MainApplication.getXmppConnection().loadAvatar(jid, nameChat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bytes ->
                    val avatar: Bitmap?
                    if (bytes != null) {
                        avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        MainApplication.getMainUIThread().post {
                            imageView.setImageBitmap(avatar)
                        }
                    }
                }, { throwable -> Log.e(throwable.message) })
        }
    }

    fun joinChat(jid: String) {
        try {
            val manager =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val entityBareJid = JidCreate.entityBareFrom(jid)
            val muc = manager.getMultiUserChat(entityBareJid)
            //todo nickname must be fix( cardV)
            val nickName = Resourcepart.from(MainApplication.getCurrentLoginCredentials().username)

            if (!muc.isJoined) {
                muc.join(nickName)
            }
        } catch (e: Exception) {
            Log.d(e.message)
        }
    }

    private fun sortChatsByRecentlyUpdatedDate() {
        chats = chats.sortedWith(Comparator { o1, o2 ->
            val timestamp1 =
                if (o1.messages.isNotEmpty()) o1.messages.sortedWith(compareByDescending { it.timestamp }).first().timestamp else -1
            val timestamp2 =
                if (o2.messages.isNotEmpty()) o2.messages.sortedWith(compareByDescending { it.timestamp }).first().timestamp else -1
            timestamp2.compareTo(timestamp1)
        }).toMutableList()
    }

    fun deleteChatOnUI(position: Int) {
        val chat = chats[position]
        recentlyDeletedItem = chat
        chats.removeAt(position)
        viewState.onItemDelete(position)
        bindedItems.remove(chat)
    }

    fun bringChatBack() {
        recentlyDeletedItem ?: return
        chats.add(recentlyDeletedItem!!)
        sortChatsByRecentlyUpdatedDate()
        viewState.onDataChange()
    }

    fun deleteChatFinally() {
        recentlyDeletedItem ?: return
        ChatListRepository.removeChat(recentlyDeletedItem!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe()
        recentlyDeletedItem = null
    }


    fun setFilter(filter: String) {
        if(filter.isBlank()){
            chats.clear()
            bindedItems.clear()
            chats.addAll(fullChats)
            viewState.onDataChange()
        }else{
            val list = fullChats.filter {
                it.chatName.contains(filter, true)
            }
            chats.clear()
            bindedItems.clear()
            chats.addAll(list)
            viewState.onDataChange()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.forEach {
            it.dispose()
        }
    }
}