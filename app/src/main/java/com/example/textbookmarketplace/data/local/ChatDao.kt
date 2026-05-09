package com.example.textbookmarketplace.data.local

import androidx.room.*
import com.example.textbookmarketplace.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE bookId = :bookId ORDER BY timestamp ASC")
    fun getMessagesForBook(bookId: String): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE receiverId = :userId AND isRead = 0")
    fun getUnreadMessages(userId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("UPDATE chat_messages SET isRead = 1 WHERE id = :messageId")
    suspend fun markAsRead(messageId: String)

    @Query("UPDATE chat_messages SET isSynced = 1 WHERE id = :messageId")
    suspend fun markSynced(messageId: String)

    @Query("SELECT * FROM chat_messages WHERE isSynced = 0")
    suspend fun getPendingMessages(): List<ChatMessage>
}