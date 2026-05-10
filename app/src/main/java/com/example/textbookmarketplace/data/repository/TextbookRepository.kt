package com.example.textbookmarketplace.data.repository

import com.example.textbookmarketplace.data.local.TextbookDao
import com.example.textbookmarketplace.data.remote.FirebaseRepository
import com.example.textbookmarketplace.domain.model.Textbook
import com.example.textbookmarketplace.domain.model.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextbookRepository @Inject constructor(
    private val localDao: TextbookDao,
    private val firebaseRepository: FirebaseRepository
) {
    fun getAllTextbooks(): Flow<List<Textbook>> = localDao.getAllTextbooks()

    fun searchTextbooks(query: String): Flow<List<Textbook>> =
        localDao.searchTextbooks(query)

    fun getMyListings(sellerId: String): Flow<List<Textbook>> =
        localDao.getMyListings(sellerId)

    suspend fun addTextbook(textbook: Textbook): UiState<Unit> {
        return try {
            val existing = localDao.countByIsbn(textbook.isbn)
            if (existing > 0) {
                return UiState.Error("A book with ISBN ${textbook.isbn} already exists")
            }
            localDao.insertTextbook(textbook)
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Failed to add textbook")
        }
    }

    suspend fun updateTextbook(textbook: Textbook): UiState<Unit> {
        return try {
            localDao.updateTextbook(textbook)
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Failed to update textbook")
        }
    }

    suspend fun deleteTextbook(textbook: Textbook): UiState<Unit> {
        return try {
            localDao.deleteTextbook(textbook)
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Failed to delete textbook")
        }
    }

    suspend fun getTextbookById(id: String): Textbook? = localDao.getTextbookById(id)

    suspend fun refreshFromRemote() {
        try {
            val remoteBooks = firebaseRepository.fetchAllTextbooks()
            remoteBooks.forEach { book ->
                localDao.insertTextbook(book.copy(isSynced = true))
            }
        } catch (e: Exception) {
            // Error handled by empty list in fetchAllTextbooks or ignored here
        }
    }
}