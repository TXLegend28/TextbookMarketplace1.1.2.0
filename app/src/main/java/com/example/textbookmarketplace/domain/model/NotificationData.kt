package com.example.textbookmarketplace.domain.model

data class NotificationData(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val bookId: String = "",
    val senderId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)