package io.moonshard.moonshard.models.dbEntities

import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique
import io.objectbox.relation.ToMany

@Entity
data class ChatEntity(
    @Id var id: Long = 0,
    @Unique var jid: String,
    var chatName: String = "",
    var isGroupChat: Boolean = false,
    var unreadMessagesCount: Int = 0
) {
    lateinit var users: ToMany<ChatUser>
    @Backlink(to = "chat")
    lateinit var messages: ToMany<MessageEntity>
}
