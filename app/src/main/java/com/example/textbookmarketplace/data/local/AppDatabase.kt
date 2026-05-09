package com.example.textbookmarketplace.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.textbookmarketplace.domain.model.ChatMessage
import com.example.textbookmarketplace.domain.model.Textbook

@Database(
    entities = [Textbook::class, ChatMessage::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun textbookDao(): TextbookDao
    abstract fun chatDao(): ChatDao
}