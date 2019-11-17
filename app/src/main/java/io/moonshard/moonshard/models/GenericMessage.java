package io.moonshard.moonshard.models;

import java.util.Date;

import io.moonshard.moonshard.MainApplication;
import io.moonshard.moonshard.models.jabber.GenericUser;
import io.moonshard.moonshard.models.roomEntities.MessageEntity;

public class GenericMessage {
    private long messageID;
    private GenericUser author;
    private long timestamp;
    private String text;
    private String imageUrl;


    public GenericMessage(MessageEntity messageEntity) {
        this.messageID = messageEntity.messageID;
        this.author = new GenericUser(messageEntity.senderJid, messageEntity.senderJid, messageEntity.senderJid);
        this.timestamp = messageEntity.timestamp;
        this.text = messageEntity.text;
        if (messageEntity.text.contains("http") && messageEntity.text.contains(".jpg")) {
            imageUrl = messageEntity.text;
        }
    }

    public String getId() {
        return String.valueOf(messageID);
    }

    public String getText() {
        return text;
    }

    public GenericUser getUser() {
        return author;
    }

    public Date getCreatedAt() {
        return new Date(timestamp);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isBelongsToCurrentUser() {
        String name = "";
        if (author.getJid().contains("/")) {
            name = author.getJid().split("/")[1] + "@" + "moonshard.tech";
        } else {
            name = author.getJid();
        }
        return name.equals(MainApplication.getCurrentLoginCredentials().username+"@"+"moonshard.tech");
    }
}
