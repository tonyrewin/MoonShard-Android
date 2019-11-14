package io.moonshard.moonshard.models.dao

import androidx.room.*
import io.moonshard.moonshard.models.roomEntities.ChatEntity


@Dao
interface ChatDao {
    @get:Query("SELECT * FROM chats")
    val allChats: List<ChatEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addChat(chatEntity: ChatEntity)

    @Query("DELETE FROM chats WHERE jid = :jid")
    fun deleteChat(jid: String)

    @Query("SELECT * FROM chats WHERE jid = :jid")
    fun getChatByChatID(jid: String): List<ChatEntity>

    @Update
    fun updateChat(chat: ChatEntity)

    @Query("DELETE FROM chats")
    fun clearChats()

    @Query("UPDATE chats SET unreadMessagesCount = :unreadMessagesCount WHERE jid = :chatID")
    fun updateUnreadMessagesCount(chatID: String, unreadMessagesCount: Int)
}