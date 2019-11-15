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
        dialogID = "ploika@moonshard.tech";
        dialogPhoto = "just";
        dialogName = "ploika";
        users = new ArrayList<>();
        unreadMessagesCount = 0;
    }

    public GenericDialog(String dialogID, String dialogPhoto,
                         String dialogName, List<GenericUser> users,
                         GenericMessage lastMessage, int unreadMessagesCount) {
        this.dialogID = dialogID;
        this.dialogPhoto = dialogPhoto;
        this.dialogName = dialogName;
        this.users = users;
        this.lastMessage = lastMessage;
        this.unreadMessagesCount = unreadMessagesCount;
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
