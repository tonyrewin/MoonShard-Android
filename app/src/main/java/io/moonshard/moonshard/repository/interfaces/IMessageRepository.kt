package io.moonshard.moonshard.repository.interfaces

import io.moonshard.moonshard.models.roomEntities.MessageEntity
import io.reactivex.Completable
import io.reactivex.Observable
import org.jxmpp.jid.Jid

interface IMessageRepository {
    fun saveMessage(messageEntity: MessageEntity): Completable
    fun getMessagesByJid(jid: Jid): Observable<List<MessageEntity>>
    fun getMessageById(id: String): Observable<MessageEntity>
    fun getLastMessage(): Observable<MessageEntity>
    fun getFirstMessage(): Observable<MessageEntity>
}