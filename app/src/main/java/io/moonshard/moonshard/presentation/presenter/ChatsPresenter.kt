package io.moonshard.moonshard.presentation.presenter

import io.moonshard.moonshard.common.BasePresenter
import io.moonshard.moonshard.common.utils.autoDispose
import io.moonshard.moonshard.models.ChatListItem
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.models.dbEntities.MessageEntity
import io.moonshard.moonshard.presentation.view.ChatsView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.repository.MessageRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import org.jxmpp.jid.impl.JidCreate
import trikita.log.Log


@InjectViewState
class ChatsPresenter : BasePresenter<ChatsView>() {

    fun observeChatList() {
        val chatsObservable = ChatListRepository.getChats()
        val messagesObservable = MessageRepository.observeMessageStorage()
        Observable.combineLatest<List<ChatEntity>, Class<Any>, Unit>(chatsObservable, messagesObservable, BiFunction { t1, _ ->

            val lastMessageSingles = emptyList<Single<MessageEntity>>().toMutableList()

            for (chat in t1) {
                lastMessageSingles.add(MessageRepository.getLastMessageSingle(JidCreate.bareFrom(chat.jid))
                    .onErrorReturn {
                        val msg = MessageEntity(
                            -1
                        )
                        msg.chat.target = chat
                        msg
                    })
            }

            Single.zip<MessageEntity, List<ChatListItem>>(lastMessageSingles) {
                val chatListItems = emptyList<ChatListItem>().toMutableList()
                for (item in it) {
                    val lastMessage = item as MessageEntity
                    chatListItems.add(
                        ChatListItem(
                            JidCreate.bareFrom(lastMessage.chat.target.jid),
                            lastMessage.chat.target.chatName,
                            lastMessage.text,
                            lastMessage.isRead,
                            lastMessage.timestamp,
                            lastMessage.isSent,
                            lastMessage.chat.target.unreadMessagesCount,
                            lastMessage.chat.target.isGroupChat
                        )
                    )
                }
                chatListItems
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.updateChatList(it)
                }, {
                    Log.e(it)
                })
                .autoDispose(this)
        })
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe()
            .autoDispose(this)
    }
}