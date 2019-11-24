package io.moonshard.moonshard.models.dao

import androidx.room.*
import io.moonshard.moonshard.models.roomEntities.ChatEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats")
    fun getAllChats(): Flowable<List<ChatEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addChat(chatEntity: ChatEntity): Completable

    @Query("DELETE FROM chats WHERE jid = :jid")
    fun deleteChat(jid: String)

    @Query("SELECT * FROM chats WHERE jid = :jid")
    fun getChatByChatID(jid: String): Flowable<List<ChatEntity>>

    @Update
    fun updateChat(chat: ChatEntity): Completable

    @Query("DELETE FROM chats")
    fun clearChats(): Completable

    @Query("UPDATE chats SET unreadMessagesCount = :unreadMessagesCount WHERE jid = :chatID")
    fun updateUnreadMessagesCount(chatID: String, unreadMessagesCount: Int): Completable
}