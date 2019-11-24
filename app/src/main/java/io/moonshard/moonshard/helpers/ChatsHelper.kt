package io.moonshard.moonshard.helpers

import androidx.lifecycle.LiveData
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.roomEntities.ChatEntity
import org.jivesoftware.smack.roster.RosterEntry

class ChatsHelper {
    fun loadLocalChats(): LiveData<List<ChatEntity>> {
        return MainApplication.getChatDB().chatDao().getAllChats()
    }

    val remoteContacts: Set<RosterEntry>?
        get() = if (MainApplication.getXmppConnection() != null) {
            MainApplication.getXmppConnection().contactList
        } else null
}