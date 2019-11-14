package io.moonshard.moonshard.models;

import java.util.ArrayList;
import java.util.List;

import io.moonshard.moonshard.models.jabber.GenericUser;
import io.moonshard.moonshard.models.roomEntities.ChatEntity;

public class GenericDialog {
    private String dialogID;
    private String dialogPhoto;
    private String dialogName;
    private List<GenericUser> users;
    private GenericMessage lastMessage;
    private int unreadMessagesCount;

    public GenericDialog(ChatEntity chatEntity) {
        dialogID = chatEntity.jid;
        dialogPhoto = chatEntity.jid;
        dialogName = chatEntity.chatName;
        users = new ArrayList<>();
        unreadMessagesCount = chatEntity.unreadMessagesCount;
    }

    public GenericDialog() {
        dialogID = "just";
        dialogPhoto = "just";
        dialogName = "just";
        users = new ArrayList<>();
        unreadMessagesCount = 0;
    }

    public String getId() {
        return dialogID;
    }

    public String getDialogPhoto() {
        return dialogPhoto;
    }

    public String getDialogName() {
        return dialogName;
    }

    public List<GenericUser> getUsers() {
        return users;
    }

    public GenericMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(GenericMessage message) {
        lastMessage = message;
    }

    public int getUnreadCount() {
        return unreadMessagesCount;
    }

    public void setUnreadMessagesCount(int unreadMessagesCount) {
        this.unreadMessagesCount = unreadMessagesCount;
    }
}
