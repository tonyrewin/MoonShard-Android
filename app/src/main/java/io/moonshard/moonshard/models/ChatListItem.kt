package io.moonshard.moonshard.models

import org.jxmpp.jid.Jid

data class ChatListItem(
    val jid: Jid,
    val chatName: String,
    val lastMessageText: String,
    val lastMessageReadState: Boolean,
    val lastMessageDate: Long,
    val lastMessageSendState: Boolean,
    val unreadMessageCount: Int,
    val isGroupChat: Boolean
)