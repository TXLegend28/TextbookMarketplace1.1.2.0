package com.example.textbookmarketplace.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "textbooks")
data class Textbook(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val author: String = "",
    val isbn: String = "",
    val edition: String = "",
    val copies: Int = 1,
    val price: Double = 0.0,
    val sellerName: String = "",
    val sellerEmail: String = "",
    val bankName: String = "",
    val accountNumber: String = "",
    val course: String = "",
    val condition: String = "Good",
    val description: String = "",
    val imageUrl: String = "",
    val localImagePath: String = "",
    val digitalFilePath: String = "",
    val digitalFileType: String = "",
    val dateAdded: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val isPendingDelete: Boolean = false,
    val sellerId: String = "",
    val category: String = "General"
)