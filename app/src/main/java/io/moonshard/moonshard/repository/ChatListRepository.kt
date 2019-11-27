package io.moonshard.moonshard.repository

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.ObjectBox
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.models.dbEntities.ChatEntity_
import io.moonshard.moonshard.repository.interfaces.IChatListRepository
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.rx.RxQuery
import io.reactivex.Completable
import io.reactivex.Observable
import org.jxmpp.jid.Jid

class ChatListRepository: IChatListRepository {
    private val chatBox: Box<ChatEntity> = ObjectBox.boxStore.boxFor()

    init {
        MainApplication.getComponent().inject(this)
    }

    override fun getChats(): Observable<List<ChatEntity>> {
        val query = chatBox.query().build()
        return RxQuery.observable(query)
    }

    override fun addChat(chatEntity: ChatEntity): Completable {
        return Completable.create {
            chatBox.put(chatEntity)
            it.onComplete()
        }
    }

    override fun getChatByJid(jid: Jid): Observable<ChatEntity> {
        return Observable.create {
            val query = chatBox.query().equal(ChatEntity_.jid, jid.asUnescapedString()).build()
            RxQuery.observable(query).subscribe { chat ->
                if (chat.isEmpty()) {
                    it.onError(Exception("Chat ${jid.asUnescapedString()} doesn't exist"))
                    return@subscribe
                }
                it.onNext(chat.first())
            }
        }
    }
}