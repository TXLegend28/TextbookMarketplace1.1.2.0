package com.example.textbookmarketplace.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.textbookmarketplace.data.local.UserPreferences
import com.example.textbookmarketplace.data.repository.ChatRepository
import com.example.textbookmarketplace.domain.model.AppUser
import com.example.textbookmarketplace.domain.model.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepo: ChatRepository,
    private val userPrefs: UserPreferences
) : ViewModel() {

    val currentUser: StateFlow<AppUser> = userPrefs.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppUser())

    fun getMessages(bookId: String): Flow<List<ChatMessage>> =
        chatRepo.getMessagesForBook(bookId)

    fun sendMessage(bookId: String, receiverId: String, message: String) {
        viewModelScope.launch {
            val user = currentUser.value
            val chatMessage = ChatMessage(
                senderId = user.id,
                senderName = user.username,
                receiverId = receiverId,
                bookId = bookId,
                message = message
            )
            chatRepo.sendMessage(chatMessage)
        }
    }

    fun markAsRead(messageId: String) {
        viewModelScope.launch { chatRepo.markAsRead(messageId) }
    }
}