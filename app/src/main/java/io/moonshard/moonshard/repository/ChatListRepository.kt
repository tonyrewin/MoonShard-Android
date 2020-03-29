package io.moonshard.moonshard.repository

import io.moonshard.moonshard.ObjectBox
import io.moonshard.moonshard.common.NotFoundException
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.models.dbEntities.ChatEntity_
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.rx.RxQuery
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate

object ChatListRepository {
    private val chatBox: Box<ChatEntity> = ObjectBox.boxStore.boxFor()

    fun getChats(): Observable<List<ChatEntity>> {
        val query = chatBox.query().build()
        return RxQuery.observable(query)
    }

    fun addChat(chatEntity: ChatEntity): Completable {
        return Completable.create {
            try {
                if (!it.isDisposed) {
                    chatBox.put(chatEntity)
                    it.onComplete()
                }
            }catch (e:Exception){
                it.onError(e)
            }
        }
    }

    fun removeChat(chatEntity: ChatEntity): Completable {
        return Completable.create {
            if (!chatBox.remove(chatEntity)) {
                if (!it.isDisposed) {
                    it.onError(NotFoundException())
                    return@create
                }
            }
            MessageRepository.removeMessagesByJid(JidCreate.bareFrom(chatEntity.jid)).subscribe {
                if (!it.isDisposed) {
                    it.onComplete()
                }
            }
        }
    }

    fun getChatByJid(jid: Jid): Single<ChatEntity> {
        return Single.create {
            val query = chatBox.query().equal(ChatEntity_.jid, jid.asUnescapedString()).build()
            RxQuery.single(query).subscribe { chat ->
                if (!it.isDisposed) {
                    if (chat.isEmpty()) {
                        it.onError(NotFoundException())
                        return@subscribe
                    }
                    it.onSuccess(chat.first())
                }
            }
        }
    }

    fun getChatByJidSingle(jid: Jid): Single<ChatEntity> {
        return Single.create {
            val query = chatBox.query().equal(ChatEntity_.jid, jid.asUnescapedString()).build()
            RxQuery.single(query).subscribe { chat ->
                if (!it.isDisposed) {
                    if (chat.isEmpty()) {
                        it.onError(NotFoundException())
                        return@subscribe
                    }
                    it.onSuccess(chat.first())
                }
            }
        }
    }

    fun changeChatName(chat: ChatEntity): Completable {
        return Completable.create {
            try {
                if (!it.isDisposed) {
                    chatBox.put(chat)
                }
                it.onComplete()
            } catch (e: Exception) {
                if (!it.isDisposed) {
                    it.onError(NotFoundException())
                }
            }
        }
    }

    fun getChatsByName(name: String): Observable<ChatEntity> {
        return Observable.create {
            val query = chatBox.query().contains(ChatEntity_.chatName, name).build()
            RxQuery.observable(query).subscribe { chat ->
                if (chat.isEmpty()) {
                    it.onError(NotFoundException())
                    return@subscribe
                }
                it.onNext(chat.first())
            }
        }
    }

    fun updateUnreadMessagesCountByJid(jid: Jid, newCountValue: Int): Completable {
        return Completable.create { completableEmitter ->
            val query = chatBox.query().equal(ChatEntity_.jid, jid.asUnescapedString()).build()
            RxQuery.single(query).subscribe { chat ->
                if (!completableEmitter.isDisposed) {
                    if (chat.isEmpty()) {
                        completableEmitter.onError(NotFoundException())
                        return@subscribe
                    }
                    chat.first().unreadMessagesCount = newCountValue
                    addChat(chat.first()).subscribe({
                        completableEmitter.onComplete()
                    }, {
                        completableEmitter.onError(it)
                    })
                }
            }
        }
    }

    fun updateUnreadMessagesCountByJid(jid: String, newCountValue: Int): Completable {
        return updateUnreadMessagesCountByJid(JidCreate.bareFrom(jid), newCountValue)
    }

    fun getUnreadMessagesCountByJid(jid: Jid): Observable<Int> {
        return Observable.create {
            val query = chatBox.query().equal(ChatEntity_.jid, jid.asUnescapedString()).build()
            RxQuery.observable(query).subscribe { chat ->
                if (chat.isEmpty()) {
                    it.onError(NotFoundException())
                    return@subscribe
                }
                it.onNext(chat.first().unreadMessagesCount)
            }
        }
    }

    fun clearChats(): Completable {
        return Completable.create {
            chatBox.removeAll()
            it.onComplete()
        }
    }
}