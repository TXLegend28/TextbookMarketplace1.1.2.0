package com.example.textbookmarketplace.data.local

import androidx.room.*
import com.example.textbookmarketplace.domain.model.Textbook
import kotlinx.coroutines.flow.Flow

@Dao
interface TextbookDao {
    @Query("SELECT * FROM textbooks WHERE isPendingDelete = 0 ORDER BY dateAdded DESC")
    fun getAllTextbooks(): Flow<List<Textbook>>

    @Query("""
        SELECT * FROM textbooks 
        WHERE isPendingDelete = 0 
        AND (title LIKE '%' || :query || '%' 
        OR author LIKE '%' || :query || '%' 
        OR sellerName LIKE '%' || :query || '%'
        OR isbn LIKE '%' || :query || '%')
    """)
    fun searchTextbooks(query: String): Flow<List<Textbook>>

    @Query("SELECT * FROM textbooks WHERE sellerId = :sellerId AND isPendingDelete = 0 ORDER BY dateAdded DESC")
    fun getMyListings(sellerId: String): Flow<List<Textbook>>

    @Query("SELECT * FROM textbooks WHERE id = :id LIMIT 1")
    suspend fun getTextbookById(id: String): Textbook?

    @Query("SELECT * FROM textbooks WHERE isbn = :isbn LIMIT 1")
    suspend fun getTextbookByIsbn(isbn: String): Textbook?

    @Query("SELECT * FROM textbooks WHERE category = :category AND isPendingDelete = 0 ORDER BY dateAdded DESC")
    fun getTextbooksByCategory(category: String): Flow<List<Textbook>>

    @Query("SELECT * FROM textbooks WHERE isSynced = 0 OR isPendingDelete = 1")
    suspend fun getPendingSyncItems(): List<Textbook>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTextbook(textbook: Textbook)

    @Update
    suspend fun updateTextbook(textbook: Textbook)

    @Delete
    suspend fun deleteTextbook(textbook: Textbook)

    @Query("UPDATE textbooks SET isPendingDelete = 1 WHERE id = :id")
    suspend fun markForDelete(id: String)

    @Query("DELETE FROM textbooks WHERE id = :id")
    suspend fun forceDelete(id: String)

    @Query("UPDATE textbooks SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("SELECT COUNT(*) FROM textbooks WHERE isbn = :isbn AND isPendingDelete = 0")
    suspend fun countByIsbn(isbn: String): Int

    @Query("UPDATE textbooks SET copies = copies - 1 WHERE id = :id AND copies > 0")
    suspend fun reduceCopies(id: String): Int
}