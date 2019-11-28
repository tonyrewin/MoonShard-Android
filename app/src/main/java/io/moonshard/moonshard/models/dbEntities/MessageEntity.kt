package io.moonshard.moonshard.models.dbEntities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
data class MessageEntity(
    @Id var id: Long = 0,
    var messageUid: String = "",
    var timestamp: Long = -1,
    var text: String = "",
    var isSent: Boolean = false,
    var isRead: Boolean = false,
    var isCurrentUserSender: Boolean = false
) {
    lateinit var chat: ToOne<ChatEntity>
    lateinit var sender: ToOne<ChatUser>
    
    override fun toString(): String {
        return text
    }
}