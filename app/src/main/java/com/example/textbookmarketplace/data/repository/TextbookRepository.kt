package com.example.textbookmarketplace.data.repository

import com.example.textbookmarketplace.data.local.TextbookDao
import com.example.textbookmarketplace.data.remote.FirebaseRepository
import com.example.textbookmarketplace.domain.model.Textbook
import com.example.textbookmarketplace.domain.model.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextbookRepository @Inject constructor(
    private val localDao: TextbookDao,
    private val remoteRepo: FirebaseRepository
) {
    fun getAllTextbooks(): Flow<List<Textbook>> = localDao.getAllTextbooks()
    fun searchTextbooks(query: String): Flow<List<Textbook>> = localDao.searchTextbooks(query)
    fun getMyListings(sellerId: String): Flow<List<Textbook>> =
        if (sellerId.isNotEmpty()) localDao.getMyListings(sellerId) else flowOf(emptyList())

    // Add ISBN Query if not in DAO yet
    suspend fun getTextbookByIsbn(isbn: String): Textbook? = localDao.getTextbookByIsbn(isbn)

    suspend fun addTextbook(textbook: Textbook): UiState<Unit> {
        val existing = localDao.countByIsbn(textbook.isbn)
        if (existing > 0) return UiState.Error("A book with ISBN ${textbook.isbn} already exists")

        localDao.insertTextbook(textbook.copy(isSynced = false))
        return try {
            val result = remoteRepo.syncTextbook(textbook)
            if (result.isSuccess) localDao.markSynced(textbook.id)
            UiState.Success(Unit)
        } catch (e: Exception) { UiState.Success(Unit) } // Optimistic update
    }

    suspend fun deleteTextbook(textbook: Textbook): UiState<Unit> {
        localDao.markForDelete(textbook.id)
        return try {
            remoteRepo.deleteFromRemote(textbook.id)
            localDao.forceDelete(textbook.id)
            UiState.Success(Unit)
        } catch (e: Exception) { UiState.Success(Unit) }
    }

    // --- NEW PURCHASE LOGIC ---
    suspend fun purchaseBook(bookId: String): UiState<Unit> {
        val book = localDao.getTextbookById(bookId) ?: return UiState.Error("Book not found")
        if (book.copies <= 0) return UiState.Error("Out of stock")

        // Update Local
        val updated = book.copy(copies = book.copies - 1, isSynced = false)
        localDao.updateTextbook(updated)

        return try {
            // Update Remote
            remoteRepo.syncTextbook(updated)
            localDao.markSynced(bookId)
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Success(Unit)
        }
    }

    suspend fun syncPendingItems() {
        val pending = localDao.getPendingSyncItems()
        pending.forEach { item ->
            if (item.isPendingDelete) {
                remoteRepo.deleteFromRemote(item.id)
                localDao.forceDelete(item.id)
            } else {
                val result = remoteRepo.syncTextbook(item)
                if (result.isSuccess) localDao.markSynced(item.id)
            }
        }
    }

    suspend fun refreshFromRemote() {
        try {
            val remote = remoteRepo.fetchAllTextbooks()
            remote.forEach { localDao.insertTextbook(it.copy(isSynced = true)) }
        } catch (_: Exception) {}
    }
}