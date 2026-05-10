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

    fun searchTextbooks(query: String): Flow<List<Textbook>> =
        localDao.searchTextbooks(query)

    fun getMyListings(sellerId: String): Flow<List<Textbook>> =
        if (sellerId.isNotEmpty()) localDao.getMyListings(sellerId)
        else flowOf(emptyList())

    suspend fun addTextbook(textbook: Textbook): UiState<Unit> {
        return try {
            // Check for duplicate ISBN
            val existing = localDao.countByIsbn(textbook.isbn)
            if (existing > 0) {
                return UiState.Error("A book with ISBN ${textbook.isbn} already exists")
            }

            // Save to local Room database first
            localDao.insertTextbook(textbook.copy(isSynced = false))

            // Sync to Firestore
            val result = remoteRepo.syncTextbook(textbook)
            if (result.isSuccess) {
                localDao.markSynced(textbook.id)
            }

            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Failed to add textbook")
        }
    }

    suspend fun updateTextbook(textbook: Textbook): UiState<Unit> {
        localDao.updateTextbook(textbook.copy(isSynced = false))
        return try {
            remoteRepo.syncTextbook(textbook)
            localDao.markSynced(textbook.id)
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Failed to update textbook")
        }
    }

    suspend fun deleteTextbook(textbook: Textbook): UiState<Unit> {
        localDao.markForDelete(textbook.id)
        return try {
            remoteRepo.deleteFromRemote(textbook.id)
            localDao.forceDelete(textbook.id)
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Failed to delete textbook")
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
                if (result.isSuccess) {
                    localDao.markSynced(item.id)
                }
            }
        }
    }

    suspend fun refreshFromRemote() {
        try {
            val remote = remoteRepo.fetchAllTextbooks()
            remote.forEach {
                localDao.insertTextbook(it.copy(isSynced = true))
            }
        } catch (_: Exception) {}
    }

    suspend fun getTextbookById(id: String): Textbook? =
        localDao.getTextbookById(id)
}