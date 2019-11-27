package io.moonshard.moonshard.models.dbEntities

import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany

@Entity
data class ChatEntity(
    @Id var id: Long = 0,
    var jid: String,
    var chatName: String,
    var isGroupChat: Boolean
) {
    lateinit var users: ToMany<ChatUser>
    @Backlink(to = "chat")
    lateinit var messages: ToMany<MessageEntity>
}
