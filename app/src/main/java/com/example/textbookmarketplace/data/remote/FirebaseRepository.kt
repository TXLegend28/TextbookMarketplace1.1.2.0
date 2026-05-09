package com.example.textbookmarketplace.data.remote

import android.net.Uri
import com.example.textbookmarketplace.domain.model.ChatMessage
import com.example.textbookmarketplace.domain.model.Textbook
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    private val listingsCollection = firestore.collection("textbooks")
    private val messagesCollection = firestore.collection("messages")

    suspend fun syncTextbook(textbook: Textbook): Result<Unit> {
        return try {
            listingsCollection.document(textbook.id).set(textbook).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteFromRemote(id: String): Result<Unit> {
        return try {
            listingsCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchAllTextbooks(): List<Textbook> {
        return try {
            listingsCollection.orderBy("dateAdded", Query.Direction.DESCENDING).get().await().toObjects(Textbook::class.java)
        } catch (e: Exception) { emptyList() }
    }

    // --- NEW UPLOAD FUNCTIONS ---

    suspend fun uploadImage(uri: Uri, bookId: String): Result<String> {
        return try {
            val ref = storage.reference.child("book_covers/$bookId.jpg")
            ref.putFile(uri).await()
            val url = ref.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun uploadDocument(uri: Uri, bookId: String, fileName: String): Result<String> {
        return try {
            val ref = storage.reference.child("book_documents/$bookId/$fileName")
            ref.putFile(uri).await()
            val url = ref.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) { Result.failure(e) }
    }
    // -----------------------------

    suspend fun sendMessage(message: ChatMessage): Result<Unit> {
        return try {
            messagesCollection.document(message.id).set(message).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    fun observeMessages(bookId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = messagesCollection.whereEqualTo("bookId", bookId).orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener { snapshot, _ ->
            snapshot?.let { trySend(it.toObjects(ChatMessage::class.java)) }
        }
        awaitClose { listener.remove() }
    }

    suspend fun markMessageRead(messageId: String) {
        try { messagesCollection.document(messageId).update("isRead", true).await() } catch (_: Exception) {}
    }
}