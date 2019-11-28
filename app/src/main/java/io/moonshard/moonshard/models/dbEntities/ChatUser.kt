package io.moonshard.moonshard.models.dbEntities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class ChatUser(
    @Id var id: Long = 0,
    val jid: String = "",
    val name: String = "",
    var lastSeen: Long = -1, // how long user is idle
    var isOnline: Boolean = false
)