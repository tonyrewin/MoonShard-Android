package io.moonshard.moonshard.models.dbEntities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique
import io.objectbox.relation.ToOne

@Entity
data class MessageEntity(
    @Id var id: Long = 0,
    @Unique var messageUid: String = "",
    @Unique var stanzaId: String? = null,
    var timestamp: Long = -1,
    var text: String = "",
    var isSent: Boolean = false,
    var isRead: Boolean = false,
    var isCurrentUserSender: Boolean = false,
    var isSystemMessage: Boolean = false,
    var isFile:Boolean=false
) {
    lateinit var chat: ToOne<ChatEntity>
    lateinit var sender: ToOne<ChatUser>
    
    override fun toString(): String {
        return text
    }
}