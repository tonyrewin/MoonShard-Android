package io.moonshard.moonshard.models;

import java.util.Date;

import io.moonshard.moonshard.models.dbEntities.MessageEntity;
import io.moonshard.moonshard.models.jabber.GenericUser;

public class GenericMessage {
    private MessageEntity messageEntity;

    public GenericMessage(MessageEntity messageEntity) {
        this.messageEntity = messageEntity;
        //   messageEntity.sender.setTarget(new ChatUser());
    }

    public String getId() {
        return messageEntity.getMessageUid();
    }

    public String getText() {
        return messageEntity.getText();
    }

    public GenericUser getUser() {
        return new GenericUser(messageEntity.sender.getTarget().getJid(), messageEntity.sender.getTarget().getName(), "");
    }

    public Date getCreatedAt() {
        return new Date(messageEntity.getTimestamp());
    }

    public String getImageUrl() {
        return "";
    }

    public boolean isBelongsToCurrentUser() {
        return messageEntity.isCurrentUserSender();
    }

    public boolean isSystemMessage() {
        return messageEntity.isSystemMessage();
    }

    public boolean isFile() {
        return messageEntity.isFile();
    }
}
