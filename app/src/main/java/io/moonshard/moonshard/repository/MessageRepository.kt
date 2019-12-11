package io.moonshard.moonshard.repository

import io.moonshard.moonshard.ObjectBox
import io.moonshard.moonshard.common.NotFoundException
import io.moonshard.moonshard.models.dbEntities.ChatEntity_
import io.moonshard.moonshard.models.dbEntities.MessageEntity
import io.moonshard.moonshard.models.dbEntities.MessageEntity_
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.objectbox.query.QueryBuilder
import io.objectbox.rx.RxQuery
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate

object MessageRepository {
    private val messageBox: Box<MessageEntity> = ObjectBox.boxStore.boxFor()

    fun saveMessage(messageEntity: MessageEntity): Completable {
        return Completable.create {
            messageBox.put(messageEntity)
            it.onComplete()
        }
    }

    fun getMessagesByJid(jid: Jid): Observable<List<MessageEntity>> {
        return Observable.create {
            ChatListRepository.getChatByJid(jid).subscribe({ chat ->
                it.onNext(chat.messages.toList())
            }, { e ->
                it.onError(e)
            })
        }
    }

    fun removeMessagesByJid(jid: Jid): Completable {
        return Completable.create {
            messageBox.query {
                link(MessageEntity_.chat).equal(ChatEntity_.jid, jid.asUnescapedString())
            }.remove()
            it.onComplete()
        }
    }

    fun getMessageById(messageUid: String): Single<MessageEntity> {
        return Single.create {
            val msg = messageBox.query {
                equal(MessageEntity_.messageUid, messageUid)
            }.findFirst()
            if (msg != null) it.onSuccess(msg) else it.onError(NotFoundException())
        }
    }

    fun getLastMessage(jid: Jid): Observable<MessageEntity> {
        return Observable.create {
            val msgQuery = messageBox.query {
                order(MessageEntity_.timestamp, QueryBuilder.DESCENDING)
                link(MessageEntity_.chat).equal(ChatEntity_.jid, jid.asUnescapedString())
            }
            RxQuery.observable(msgQuery).subscribe({ message ->
                if (message.isNotEmpty()) {
                    it.onNext(message.first())
                } else {
                    it.onError(NotFoundException())
                }
            }, { e ->
                it.onError(e)
            })
        }
    }

    /**
     * Get first message in chat
     * @param jid
     */
    fun getFirstMessage(jid: Jid): Observable<MessageEntity> {
        return Observable.create {
            val msgQuery = messageBox.query {
                order(MessageEntity_.timestamp)
                link(MessageEntity_.chat).equal(ChatEntity_.jid, jid.asUnescapedString())
            }
            RxQuery.observable(msgQuery).subscribe({ message ->
                if (message.isNotEmpty()) {
                    it.onNext(message.first())
                } else {
                    it.onError(NotFoundException())
                }
            }, { e ->
                it.onError(e)
            })
        }
    }

    /**
     * Get first message in chat
     * @param jid String representation of chat JID
     */
    fun getFirstMessage(jid: String): Observable<MessageEntity> {
        return getFirstMessage(JidCreate.bareFrom(jid))
    }

    fun getRealUnreadMessagesCountByJid(jid: Jid): Observable<Int> {
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

    fun updateRealUnreadMessagesCount(jid: Jid): Completable {
        return Completable.create {
            val msgQuery = messageBox.query {
                equal(MessageEntity_.isCurrentUserSender, false)
                equal(MessageEntity_.isRead, false)
                link(MessageEntity_.chat).equal(ChatEntity_.jid, jid.asUnescapedString())
            }

            RxQuery.single(msgQuery).subscribe({ unreadMessages ->
                unreadMessages.forEach {
                    it.isRead = true
                }
                messageBox.put(unreadMessages)
                it.onComplete()
            }, { e ->
                it.onError(e)
            })
        }
    }

    fun updateRealUnreadMessagesCount(jid: String): Completable {
        return updateRealUnreadMessagesCount(JidCreate.bareFrom(jid))
    }

    fun clearMessages(): Completable {
        return Completable.create {
            messageBox.removeAll()
            it.onComplete()
        }
    }
}