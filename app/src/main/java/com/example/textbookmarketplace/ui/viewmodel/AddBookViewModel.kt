package com.example.textbookmarketplace.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.textbookmarketplace.data.local.UserPreferences
import com.example.textbookmarketplace.data.remote.FirebaseRepository
import com.example.textbookmarketplace.data.repository.TextbookRepository
import com.example.textbookmarketplace.domain.model.AppUser
import com.example.textbookmarketplace.domain.model.Textbook
import com.example.textbookmarketplace.domain.model.UiState
import com.example.textbookmarketplace.utils.FileHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val repository: TextbookRepository,
    private val userPrefs: UserPreferences,
    private val fileHelper: FileHelper,
    private val remoteRepo: FirebaseRepository // Inject for uploads
) : ViewModel() {

    val currentUser: StateFlow<AppUser> = userPrefs.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppUser())

    private val _addState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val addState: StateFlow<UiState<Unit>> = _addState.asStateFlow()

    // Local file tracking
    private var localImagePath: String = ""
    private var localImageUri: Uri? = null
    private var digitalFilePath: String = ""
    private var digitalFileType: String = ""
    private var digitalFileUri: Uri? = null

    fun setImage(uri: Uri) {
        localImageUri = uri
        viewModelScope.launch {
            localImagePath = fileHelper.saveImage(uri) ?: ""
        }
    }

    fun setDocument(uri: Uri) {
        digitalFileUri = uri
        viewModelScope.launch {
            digitalFilePath = fileHelper.saveDocument(uri) ?: ""
            digitalFileType = fileHelper.getFileTypeFromPath(digitalFilePath) ?: ""
        }
    }

    fun addTextbook(
        title: String,
        author: String,
        isbn: String,
        edition: String,
        copies: Int,
        price: Double,
        course: String,
        condition: String,
        description: String,
        category: String
    ) {
        viewModelScope.launch {
            _addState.value = UiState.Loading

            try {
                val user = currentUser.value

                // 1. Create initial textbook object
                var textbook = Textbook(
                    title = title,
                    author = author,
                    isbn = isbn,
                    edition = edition,
                    copies = copies,
                    price = price,
                    sellerName = user.username,
                    sellerEmail = user.email,
                    bankName = "",
                    accountNumber = "",
                    course = course,
                    condition = condition,
                    description = description,
                    sellerId = user.id,
                    category = category,
                    digitalFileType = digitalFileType
                )

                // 2. Upload cover image to Firebase Storage (if exists)
                var imageUrl = ""
                if (localImageUri != null) {
                    val imageResult = remoteRepo.uploadImage(localImageUri!!, textbook.id)
                    if (imageResult.isSuccess) {
                        imageUrl = imageResult.getOrNull() ?: ""
                    }
                }

                // 3. Upload digital document to Firebase Storage (if exists)
                var digitalUrl = ""
                if (digitalFileUri != null && digitalFileType.isNotEmpty()) {
                    val docResult = remoteRepo.uploadDocument(
                        digitalFileUri!!,
                        textbook.id,
                        digitalFileType
                    )
                    if (docResult.isSuccess) {
                        digitalUrl = docResult.getOrNull() ?: ""
                    }
                }

                // 4. Update textbook with cloud URLs
                textbook = textbook.copy(
                    imageUrl = imageUrl,
                    localImagePath = localImagePath, // Keep local path for offline caching
                    digitalFilePath = digitalUrl
                )

                // 5. Save to repository (Room + Firestore)
                val result = repository.addTextbook(textbook)
                _addState.value = result

            } catch (e: Exception) {
                _addState.value = UiState.Error(e.message ?: "Failed to add textbook")
            }
        }
    }

    fun resetState() {
        _addState.value = UiState.Empty
        localImagePath = ""
        localImageUri = null
        digitalFilePath = ""
        digitalFileType = ""
        digitalFileUri = null
    }
}