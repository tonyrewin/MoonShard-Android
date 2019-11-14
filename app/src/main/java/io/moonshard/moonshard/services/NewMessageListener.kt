package io.moonshard.moonshard.services

import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.packet.Message
import org.jxmpp.jid.EntityBareJid

interface NewMessageListener  {

    fun text(from: EntityBareJid, message: Message,  chat: Chat)
}