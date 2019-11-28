package io.moonshard.moonshard.repository.interfaces

import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import org.jxmpp.jid.Jid

interface IChatListRepository {
    fun getChats(): Observable<List<ChatEntity>>
    fun addChat(chatEntity: ChatEntity): Completable
    fun getChatByJid(jid: Jid): Observable<ChatEntity>
    fun getUnreadMessagesCountByJid(jid: Jid): Observable<Int>
    fun updateUnreadMessagesCountByJid(jid: Jid, newCountValue: Int): Completable
}