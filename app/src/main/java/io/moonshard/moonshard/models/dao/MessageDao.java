package io.moonshard.moonshard.models.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.moonshard.moonshard.models.roomEntities.MessageEntity;

@Dao
public interface MessageDao {
    @Insert
    long insertMessage(MessageEntity chatModel);

    @Query("DELETE FROM messages WHERE messageID = :messageID")
    void deleteMessage(String messageID);

    @Query("DELETE FROM messages WHERE chatID = :jid")
    void deleteMessagesByChatID(String jid);

    @Query("SELECT * FROM messages WHERE chatID = :jid")
    List<MessageEntity> getMessagesByChatID(String jid);

    @Query("SELECT * FROM messages WHERE messageID = :messageID")
    List<MessageEntity> getMessageByID(long messageID);

    @Update
    void updateMessage(MessageEntity message);

    @Query("DELETE FROM messages")
    void clearMessages();

    @Query("DELETE FROM messages WHERE chatID = :chatID")
    void clearMessagesByChatID(String chatID);

    @Query("SELECT messageID FROM messages WHERE chatID = :chatID GROUP BY :chatID HAVING MAX(timestamp)")
    long getLastMessageByChatID(String chatID);

    @Query("SELECT messageID FROM messages WHERE chatID = :chatID GROUP BY :chatID HAVING MIN(timestamp)")
    long getFirstMessageByChatID(String chatID);

    @Query("SELECT * FROM messages WHERE messageUid = :uid")
    List<MessageEntity> getMessageByUID(String uid);
}
