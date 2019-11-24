package io.moonshard.moonshard.models.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.moonshard.moonshard.models.roomEntities.MessageEntity;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface MessageDao {
    @Insert
    Single<Long> insertMessage(MessageEntity messageEntity);

    @Query("DELETE FROM messages WHERE messageID = :messageID")
    Completable deleteMessage(String messageID);

    @Query("DELETE FROM messages WHERE chatID = :jid")
    Completable deleteMessagesByChatID(String jid);

    @Query("SELECT * FROM messages WHERE chatID = :jid")
    Flowable<List<MessageEntity>> getMessagesByChatID(String jid);

    @Query("SELECT * FROM messages WHERE messageID = :messageID")
    Flowable<List<MessageEntity>> getMessageByID(long messageID);

    @Update
    Completable updateMessage(MessageEntity message);

    @Query("DELETE FROM messages")
    Completable clearMessages();

    @Query("DELETE FROM messages WHERE chatID = :chatID")
    Completable clearMessagesByChatID(String chatID);

    @Query("SELECT * FROM messages WHERE chatID = :chatID GROUP BY :chatID HAVING MAX(timestamp)")
    Flowable<List<MessageEntity>> getLastMessageByChatID(String chatID);

    @Query("SELECT * FROM messages WHERE chatID = :chatID GROUP BY :chatID HAVING MIN(timestamp)")
    Maybe<MessageEntity> getFirstMessageByChatID(String chatID);

    @Query("SELECT * FROM messages WHERE messageUid = :uid")
    Flowable<List<MessageEntity>> getMessageByUID(String uid);
}
