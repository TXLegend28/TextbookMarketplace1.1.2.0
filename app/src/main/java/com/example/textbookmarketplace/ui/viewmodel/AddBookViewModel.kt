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
    private val remoteRepo: FirebaseRepository // Inject Remote Repo
) : ViewModel() {

    val currentUser: StateFlow<AppUser> = userPrefs.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppUser())

    private val _addState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val addState: StateFlow<UiState<Unit>> = _addState.asStateFlow()

    private var localImagePath: String = ""
    private var localImageUri: Uri? = null
    private var digitalFilePath: String = ""
    private var digitalFileType: String = ""
    private var digitalFileUri: Uri? = null

    fun setImage(uri: Uri) {
        localImageUri = uri
        localImagePath = fileHelper.saveImage(uri) ?: ""
    }

    fun setDocument(uri: Uri) {
        digitalFileUri = uri
        digitalFilePath = fileHelper.saveDocument(uri) ?: ""
        digitalFileType = fileHelper.getFileTypeFromPath(digitalFilePath) ?: ""
    }

    // Updated to handle Uploads
    fun addTextbook(
        title: String, author: String, isbn: String, edition: String,
        copies: Int, price: Double, course: String, condition: String,
        description: String, category: String,
        bankName: String, accountNumber: String
    ) {
        viewModelScope.launch {
            _addState.value = UiState.Loading
            val user = currentUser.value

            // 1. Create Initial Object
            var textbook = Textbook(
                title = title, author = author, isbn = isbn, edition = edition,
                copies = copies, price = price,
                sellerName = user.username, sellerEmail = user.email,
                bankName = bankName, accountNumber = accountNumber,
                course = course, condition = condition, description = description,
                sellerId = user.id, category = category
            )

            // 2. Upload Image if exists
            var imageUrl = ""
            if (localImageUri != null) {
                val result = remoteRepo.uploadImage(localImageUri!!, textbook.id)
                if (result.isSuccess) imageUrl = result.getOrNull() ?: ""
            }

            // 3. Upload Document if exists
            var docUrl = ""
            if (digitalFileUri != null) {
                val fileName = "doc.${digitalFileType}"
                val result = remoteRepo.uploadDocument(digitalFileUri!!, textbook.id, fileName)
                if (result.isSuccess) docUrl = result.getOrNull() ?: ""
            }

            // 4. Update object with Cloud URLs
            textbook = textbook.copy(
                imageUrl = imageUrl,
                localImagePath = localImagePath, // Keep local path for caching
                digitalFilePath = docUrl,
                digitalFileType = digitalFileType
            )

            // 5. Save to Repo
            _addState.value = repository.addTextbook(textbook)
        }
    }

    fun resetState() {
        _addState.value = UiState.Empty
        localImagePath = ""; localImageUri = null
        digitalFilePath = ""; digitalFileType = ""; digitalFileUri = null
    }
}