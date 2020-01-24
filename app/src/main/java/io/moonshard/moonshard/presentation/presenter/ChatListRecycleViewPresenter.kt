package io.moonshard.moonshard.presentation.presenter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.DateHolder
import io.moonshard.moonshard.models.ChatListItem
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
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import trikita.log.Log
import java.util.*

@InjectViewState
class ChatListRecycleViewPresenter : MvpPresenter<ChatListRecyclerView>() {
    private var chats = emptyList<ChatListItem>().toMutableList()
    private var fullChats = emptyList<ChatListItem>().toMutableList()

    private val recentlyDeletedItems = mutableMapOf<Jid, ChatListItem>()

    fun getChatListSize(): Int {
        return chats.size
    }

    fun onBindViewHolder(
        holder: ChatListAdapter.ChatListViewHolder,
        position: Int,
        listener: ChatListListener
    ) {
        try {
            val chat = chats[position]
            MainApplication.getXmppConnection().addChatStatusListener(chat.jid.asUnescapedString())
            if (chat.isGroupChat) {
                joinChat(chat.jid.asUnescapedString())
            } else {
                MainApplication.getXmppConnection().chatManager.chatWith(JidCreate.entityBareFrom(chat.jid))
            }
            setAvatar(chat.jid.asUnescapedString(), chat.chatName, holder.avatar)
            holder.chatName.visibility = View.VISIBLE
            holder.chatName.text = chat.chatName
            if (chat.unreadMessageCount > 0) {
                holder.unreadMessageCount.visibility = View.VISIBLE
                holder.unreadMessageCount.text = chat.unreadMessageCount.toString()
            } else {
                holder.unreadMessageCount.visibility = View.INVISIBLE
            }

            if (chat.lastMessageText.isNotEmpty()) {
                holder.lastMessageText.visibility = View.VISIBLE
                holder.lastMessageDate.visibility = View.VISIBLE
                holder.lastMessageReadState.visibility = View.VISIBLE

                holder.lastMessageText.text = chat.lastMessageText

                val currentDate = DateHolder(System.currentTimeMillis())
                val messageDate = DateHolder(chat.lastMessageDate)
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
                holder.lastMessageReadState.setImageResource(if (chat.lastMessageReadState && chat.lastMessageReadState) R.drawable.ic_checked_message_state else R.drawable.ic_sent_message_state)
                holder.lastMessageDate.text = lastMessageDateText
            } else {
                holder.lastMessageText.visibility = View.INVISIBLE
                holder.lastMessageDate.visibility = View.INVISIBLE
                holder.lastMessageReadState.visibility = View.INVISIBLE
            }

            holder.itemView.setOnClickListener {
                listener.clickChat(chat)
            }
        } catch (e: Exception) {
            Log.d(e)
        }
    }

    fun setData(chats: List<ChatListItem>) {
        this.chats = chats.toMutableList()
        this.fullChats = chats.toMutableList()
        sortChatsByRecentlyUpdatedDate()
        viewState.onDataChange()
    }

    @SuppressLint("CheckResult")
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
            val timestamp1 = o1.lastMessageDate
            val timestamp2 = o2.lastMessageDate
            timestamp2.compareTo(timestamp1)
        }).toMutableList()
    }

    fun deleteChatOnUI(position: Int): ChatListItem {
        val chat = chats[position]
        recentlyDeletedItems[chat.jid] = chat
        chats.removeAt(position)
        viewState.onItemDelete(position)
        return chat
    }

    fun bringChatBack(jid: Jid) {
        recentlyDeletedItems[jid] ?: return

        chats.add(recentlyDeletedItems[jid]!!)
        recentlyDeletedItems.remove(jid)
        sortChatsByRecentlyUpdatedDate()
        viewState.onDataChange()
    }

    @SuppressLint("CheckResult")
    fun deleteChatFinally(jid: Jid) {
        recentlyDeletedItems[jid] ?: return

        ChatListRepository.getChatByJidSingle(recentlyDeletedItems[jid]!!.jid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                ChatListRepository.removeChat(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({}, {
                        Log.d("Chat ${jid.asUnescapedString()} not found")
                    })
                Log.d("Chat ${jid.asUnescapedString()} was deleted")
                recentlyDeletedItems.remove(jid)
                viewState.onDataChange()
            }, {
                Log.d("Chat ${jid.asUnescapedString()} not found")
            })
    }


    fun setFilter(filter: String) {
        if(filter.isBlank()){
            chats.clear()
            chats.addAll(fullChats)
            viewState.onDataChange()
        }else{
            val list = fullChats.filter {
                it.chatName.contains(filter, true)
            }
            chats.clear()
            chats.addAll(list)
            viewState.onDataChange()
        }
    }
}