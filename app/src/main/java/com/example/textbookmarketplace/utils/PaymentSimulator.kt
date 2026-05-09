package com.example.textbookmarketplace.utils

import com.example.textbookmarketplace.domain.model.PaymentStatus
import com.example.textbookmarketplace.domain.model.PaymentTransaction
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentSimulator @Inject constructor() {

    suspend fun processPayment(
        phoneNumber: String,
        amount: Double,
        bookTitle: String
    ): PaymentTransaction {
        // Simulate network delay
        delay(2000)

        // Simulate M-Pesa STK Push
        val transaction = PaymentTransaction(
            id = UUID.randomUUID().toString(),
            amount = amount,
            phoneNumber = phoneNumber,
            status = PaymentStatus.PROCESSING,
            referenceCode = "TM${System.currentTimeMillis()}",
            bookTitle = bookTitle
        )

        // Simulate processing
        delay(3000)

        // 90% success rate simulation
        return transaction.copy(
            status = if (Math.random() > 0.1) PaymentStatus.SUCCESS else PaymentStatus.FAILED
        )
    }
}