package io.moonshard.moonshard.helpers;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.moonshard.moonshard.MainApplication;
import io.moonshard.moonshard.models.jabber.GenericUser;
import io.moonshard.moonshard.models.roomEntities.ChatEntity;
import io.moonshard.moonshard.models.roomEntities.MessageEntity;

public class LocalDBWrapper {
    private static final String LOG_TAG = "LocalDBWrapper";
    private static RoomHelper dbInstance = MainApplication.getChatDB();

    public static void createChatEntry(String jid, String chatName, ArrayList<GenericUser> users) {
        dbInstance.chatDao().addChat(new ChatEntity(jid, chatName, users, 0, ""));
    }

    public static long createMessageEntry(String chatID, String messageUid, String senderJid, long timestamp, String text, boolean isSent, boolean isRead) {
        List<ChatEntity> chatEntities = MainApplication.getChatDB().chatDao().getChatByChatID(chatID);
        if(chatEntities.size() < 1) {
            Log.e(LOG_TAG, "Failed to create message entry because chat " + chatID + " doesn't exists!");
            return -1;
        }
        MessageEntity message = new MessageEntity(chatID, messageUid, senderJid, timestamp, text, isSent, isRead);
        long index = dbInstance.messageDao().insertMessage(message);
        return index;
    }

    public static MessageEntity getMessageByID(long messageID) {
        List<MessageEntity> messages = dbInstance.messageDao().getMessageByID(messageID);
        if(messages.isEmpty()) {
            return null;
        }
        return messages.get(0);
    }

    public static MessageEntity getMessageByUID(String messageUID) {
        List<MessageEntity> messages = dbInstance.messageDao().getMessageByUID(messageUID);
        if(messages.isEmpty()) {
            return null;
        }
        return messages.get(0);
    }

    public static List<MessageEntity> getMessagesByChatID(String chatID) {
        List<MessageEntity> messages = dbInstance.messageDao().getMessagesByChatID(chatID);
        if(messages.isEmpty()) {
            return null;
        }
        return messages;
    }

    public static ChatEntity getChatByChatID(String chatID) {
        List<ChatEntity> chats = dbInstance.chatDao().getChatByChatID(chatID);
        if(chats.isEmpty()) {
            return null;
        }
        return chats.get(0);
    }

    public static void updateChatEntity(ChatEntity chatEntity) {
        dbInstance.chatDao().updateChat(chatEntity);
    }

    public static void updateMessage(MessageEntity messageEntity) {
        dbInstance.messageDao().updateMessage(messageEntity);
    }

    public static void clearDatabase() {
        dbInstance.messageDao().clearMessages();
        dbInstance.chatDao().clearChats();
    }

    public static MessageEntity getLastMessage(String chatID) {
        long messageID = dbInstance.messageDao().getLastMessageByChatID(chatID);
        return getMessageByID(messageID);
    }

    public static MessageEntity getFirstMessage(String chatID) {
        long messageID = dbInstance.messageDao().getFirstMessageByChatID(chatID);
        return getMessageByID(messageID);
    }

    public static void updateChatUnreadMessagesCount(String chatID, int unreadMessagesCount) {
        dbInstance.chatDao().updateUnreadMessagesCount(chatID, unreadMessagesCount);
    }

    public static void clearChat(String chatID) {
        dbInstance.messageDao().clearMessagesByChatID(chatID);
    }
}
