package io.moonshard.moonshard.repository.interfaces

import io.moonshard.moonshard.models.dbEntities.MessageEntity
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.jxmpp.jid.Jid

interface IMessageRepository {
    fun saveMessage(messageEntity: MessageEntity): Completable
    fun getMessagesByJid(jid: Jid): Observable<List<MessageEntity>>
    fun removeMessagesByJid(jid: Jid): Completable
    fun getMessageById(messageUid: String): Single<MessageEntity>
    fun getLastMessage(jid: Jid): Observable<MessageEntity>
    fun getFirstMessage(jid: Jid): Observable<MessageEntity>
    fun getRealUnreadMessagesCountByJid(jid: Jid): Observable<Int>

    // FIXME this one should update specific message instead of all messages
    fun updateRealUnreadMessagesCount(jid: Jid): Completable
}