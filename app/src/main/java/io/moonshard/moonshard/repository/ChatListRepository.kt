package io.moonshard.moonshard.repository

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.helpers.RoomHelper
import io.moonshard.moonshard.models.roomEntities.ChatEntity
import io.moonshard.moonshard.repository.interfaces.IChatListRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import org.jxmpp.jid.Jid
import javax.inject.Inject

class ChatListRepository: IChatListRepository {
    @Inject
    lateinit var roomHelper: RoomHelper

    init {
        MainApplication.getComponent().inject(this)
    }

    override fun getChats(): Flowable<List<ChatEntity>> {
        return roomHelper.chatDao().getAllChats()
    }

    override fun addChat(chatEntity: ChatEntity): Completable {
        return Completable.create {
            roomHelper.chatDao().addChat(chatEntity).subscribe({
                it.onComplete()
            }, { e ->
                it.onError(e)
            })
        }
    }

    override fun getChatByJid(jid: Jid): Observable<ChatEntity> {
        return Observable.create {
            roomHelper.chatDao().getChatByChatID(jid.asUnescapedString()).subscribe { chat ->
                if (chat.isEmpty()) {
                    it.onError(Exception("Chat ${jid.asUnescapedString()} doesn't exist"))
                    return@subscribe
                }
                it.onNext(chat.first())
            }
        }
    }
}