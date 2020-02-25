package io.moonshard.moonshard.models;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
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

    public boolean isImage(){
        return isImageFile(messageEntity.getText());
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public  String getFileNameFromURL() {
        String url = messageEntity.getText();

        if (url == null) {
            return "";
        }
        try {
            URL resource = new URL(url);
            String host = resource.getHost();
            if (host.length() > 0 && url.endsWith(host)) {
                // handle ...example.com
                return "";
            }
        }
        catch(MalformedURLException e) {
            return "";
        }

        int startIndex = url.lastIndexOf('/') + 1;
        int length = url.length();

        // find end index for ?
        int lastQMPos = url.lastIndexOf('?');
        if (lastQMPos == -1) {
            lastQMPos = length;
        }

        // find end index for #
        int lastHashPos = url.lastIndexOf('#');
        if (lastHashPos == -1) {
            lastHashPos = length;
        }

        // calculate the end index
        int endIndex = Math.min(lastQMPos, lastHashPos);
        try {
            return URLDecoder.decode(url.substring(startIndex, endIndex),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
