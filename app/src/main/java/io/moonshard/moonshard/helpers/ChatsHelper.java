package io.moonshard.moonshard.helpers;

import org.jivesoftware.smack.roster.RosterEntry;

import java.util.List;
import java.util.Set;

import io.moonshard.moonshard.MainApplication;
import io.moonshard.moonshard.models.roomEntities.ChatEntity;

 public  class ChatsHelper {

    public  List<ChatEntity> loadLocalChats() {
        return MainApplication.getChatDB().chatDao().getAllChats();
    }

    public  Set<RosterEntry> getRemoteContacts() {
        if(MainApplication.getXmppConnection() != null) {
            return MainApplication.getXmppConnection().getContactList();
        }
        return null;
    }

}
