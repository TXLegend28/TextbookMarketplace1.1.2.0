package com.example.textbookmarketplace.domain.model

data class PaymentTransaction(
    val id: String = "",
    val bookId: String = "",
    val bookTitle: String = "",
    val buyerId: String = "",
    val sellerId: String = "",
    val amount: Double = 0.0,
    val phoneNumber: String = "",
    val status: PaymentStatus = PaymentStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis(),
    val referenceCode: String = ""
)

enum class PaymentStatus {
    PENDING,
    PROCESSING,
    SUCCESS,
    FAILED,
    CANCELLED
}