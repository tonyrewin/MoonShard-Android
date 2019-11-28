package io.moonshard.moonshard.repository

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.ObjectBox
import io.moonshard.moonshard.models.dbEntities.ChatEntity_
import io.moonshard.moonshard.models.dbEntities.ChatUser_
import io.moonshard.moonshard.models.dbEntities.MessageEntity
import io.moonshard.moonshard.models.dbEntities.MessageEntity_
import io.moonshard.moonshard.repository.interfaces.IMessageRepository
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.objectbox.query.QueryBuilder
import io.objectbox.rx.RxQuery
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.jxmpp.jid.Jid
import java.util.*

class MessageRepository: IMessageRepository {
    private val chatListRepository = ChatListRepository()
    private val messageBox: Box<MessageEntity> = ObjectBox.boxStore.boxFor()

    init {
        MainApplication.getComponent().inject(this)
    }

    override fun saveMessage(messageEntity: MessageEntity): Completable {
        return Completable.create {
            messageBox.put(messageEntity)
            it.onComplete()
        }
    }

    override fun getMessagesByJid(jid: Jid): Observable<List<MessageEntity>> {
        return Observable.create {
            chatListRepository.getChatByJid(jid).subscribe({ chat ->
                it.onNext(chat.messages.toList())
            }, { e ->
                it.onError(e)
            })
        }
    }

    override fun getMessageById(messageUid: String): Single<MessageEntity> {
        return Single.create {
            val msg = messageBox.query {
                equal(MessageEntity_.messageUid, messageUid)
            }.findFirst()
            if (msg != null) it.onSuccess(msg) else it.onError(Exception("Message doesn't exist"))
        }
    }

    override fun getLastMessage(jid: Jid): Observable<MessageEntity> {
        return Observable.create {
            val msgQuery = messageBox.query {
                order(MessageEntity_.timestamp, QueryBuilder.DESCENDING)
                link(MessageEntity_.chat).equal(ChatEntity_.jid, jid.asUnescapedString())
            }
            RxQuery.observable(msgQuery).subscribe({ message ->
                if (message.isNotEmpty()) {
                    it.onNext(message.first())
                } else {
                    it.onError(Exception("Chat doesn't exist or chat is empty"))
                }
            }, { e ->
                it.onError(e)
            })
        }
    }

    override fun getFirstMessage(jid: Jid): Observable<MessageEntity> {
        return Observable.create {
            val msgQuery = messageBox.query {
                order(MessageEntity_.timestamp)
                link(MessageEntity_.chat).equal(ChatEntity_.jid, jid.asUnescapedString())
            }
            RxQuery.observable(msgQuery).subscribe({ message ->
                if (message.isNotEmpty()) {
                    it.onNext(message.first())
                } else {
                    it.onError(Exception("Chat doesn't exist or chat is empty"))
                }
            }, { e ->
                it.onError(e)
            })
        }
    }

    override fun getRealUnreadMessagesCountByJid(jid: Jid): Observable<Int> {
        return Observable.create {
            val msgQuery = messageBox.query {
                equal(MessageEntity_.isCurrentUserSender, false)
                equal(MessageEntity_.isRead, false)
                link(MessageEntity_.chat).equal(ChatEntity_.jid, jid.asUnescapedString())
            }

            RxQuery.observable(msgQuery).subscribe({ unreadMessages ->
                if (unreadMessages.isNotEmpty()) {
                    it.onNext(unreadMessages.size)
                } else {
                    it.onNext(0)
                }
            }, { e ->
                it.onError(e)
            })
        }
    }

    override fun updateRealUnreadMessagesCount(jid: Jid): Completable {
        return Completable.create {
            val msgQuery = messageBox.query {
                equal(MessageEntity_.isCurrentUserSender, false)
                equal(MessageEntity_.isRead, false)
                link(MessageEntity_.chat).equal(ChatEntity_.jid, jid.asUnescapedString())
            }

            RxQuery.observable(msgQuery).subscribe({ unreadMessages ->
                unreadMessages.forEach {
                    it.isRead = false
                }
                messageBox.put(unreadMessages)
                it.onComplete()
            }, { e ->
                it.onError(e)
            })
        }
    }
}