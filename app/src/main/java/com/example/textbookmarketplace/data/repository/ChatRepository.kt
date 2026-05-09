package com.example.textbookmarketplace.data.repository

import com.example.textbookmarketplace.data.local.ChatDao
import com.example.textbookmarketplace.data.remote.FirebaseRepository
import com.example.textbookmarketplace.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val localDao: ChatDao,
    private val remoteRepo: FirebaseRepository
) {
    fun getMessagesForBook(bookId: String): Flow<List<ChatMessage>> =
        localDao.getMessagesForBook(bookId)

    fun getUnreadMessages(userId: String): Flow<List<ChatMessage>> =
        localDao.getUnreadMessages(userId)

    suspend fun sendMessage(message: ChatMessage) {
        localDao.insertMessage(message.copy(isSynced = false))
        try {
            val result = remoteRepo.sendMessage(message)
            if (result.isSuccess) {
                localDao.markSynced(message.id)
            }
        } catch (_: Exception) {}
    }

    suspend fun markAsRead(messageId: String) {
        localDao.markAsRead(messageId)
        try {
            remoteRepo.markMessageRead(messageId)
        } catch (_: Exception) {}
    }
}