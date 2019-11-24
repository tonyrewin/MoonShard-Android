package io.moonshard.moonshard.repository

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.helpers.RoomHelper
import io.moonshard.moonshard.models.roomEntities.MessageEntity
import io.moonshard.moonshard.repository.interfaces.IMessageRepository
import io.reactivex.Completable
import io.reactivex.Observable
import org.jxmpp.jid.Jid
import javax.inject.Inject

class MessageRepository(): IMessageRepository {
    @Inject
    lateinit var roomHelper: RoomHelper

    init {
        MainApplication.getComponent().inject(this)
    }

    override fun saveMessage(messageEntity: MessageEntity): Completable {
        return Completable.create {
            roomHelper.chatDao().getChatByChatID(messageEntity.chatID).subscribe { chat ->
                if (chat.isEmpty()) {
                    it.onError(Exception("Failed to create message entry because chat ${messageEntity.chatID} doesn't exists!"))
                    return@subscribe
                }
                roomHelper.messageDao().insertMessage(messageEntity).subscribe { _ ->
                    it.onComplete()
                }
            }
        }
    }

    override fun getMessagesByJid(jid: Jid): Observable<List<MessageEntity>> {
        TODO("not implemented")
    }

    override fun getMessageById(id: String): Observable<MessageEntity> {
        TODO("not implemented")
    }

    override fun getLastMessage(): Observable<MessageEntity> {
        TODO("not implemented")
    }

    override fun getFirstMessage(): Observable<MessageEntity> {
        TODO("not implemented")
    }
}