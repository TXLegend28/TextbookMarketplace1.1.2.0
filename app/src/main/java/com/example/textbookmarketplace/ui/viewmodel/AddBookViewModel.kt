package com.example.textbookmarketplace.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.textbookmarketplace.data.local.UserPreferences
import com.example.textbookmarketplace.data.repository.TextbookRepository
import com.example.textbookmarketplace.domain.model.AppUser
import com.example.textbookmarketplace.domain.model.Textbook
import com.example.textbookmarketplace.domain.model.UiState
import com.example.textbookmarketplace.utils.FileHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val repository: TextbookRepository,
    private val userPrefs: UserPreferences,
    private val fileHelper: FileHelper
) : ViewModel() {

    val currentUser: StateFlow<AppUser> = userPrefs.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppUser())

    private val _addState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val addState: StateFlow<UiState<Unit>> = _addState.asStateFlow()

    private val _editingTextbook = MutableStateFlow<Textbook?>(null)
    val editingTextbook: StateFlow<Textbook?> = _editingTextbook.asStateFlow()

    private var localImagePath: String = ""
    private var digitalFilePath: String = ""
    private var digitalFileType: String = ""

    fun loadTextbook(bookId: String) {
        viewModelScope.launch {
            val book = repository.getTextbookById(bookId)
            book?.let {
                _editingTextbook.value = it
                localImagePath = it.localImagePath
                digitalFilePath = it.digitalFilePath
                digitalFileType = it.digitalFileType
            }
        }
    }

    fun setImage(uri: Uri) {
        viewModelScope.launch {
            localImagePath = fileHelper.saveImage(uri) ?: ""
        }
    }

    fun setDocument(uri: Uri) {
        viewModelScope.launch {
            digitalFilePath = fileHelper.saveDocument(uri) ?: ""
            digitalFileType = fileHelper.getFileTypeFromPath(digitalFilePath) ?: ""
        }
    }

    fun saveTextbook(
        id: String? = null,
        title: String, author: String, isbn: String, edition: String,
        copies: Int, price: Double, course: String, condition: String,
        description: String, category: String,
        sellerName: String, sellerEmail: String,
        bankName: String, accountNumber: String
    ) {
        viewModelScope.launch {
            _addState.value = UiState.Loading
            val user = currentUser.value

            val textbook = Textbook(
                id = id ?: UUID.randomUUID().toString(),
                title = title, author = author, isbn = isbn, edition = edition,
                copies = copies, price = price,
                sellerName = sellerName, sellerEmail = sellerEmail,
                bankName = bankName, accountNumber = accountNumber,
                course = course, condition = condition, description = description,
                localImagePath = localImagePath,
                digitalFilePath = digitalFilePath, digitalFileType = digitalFileType,
                sellerId = user.id, category = category,
                dateAdded = _editingTextbook.value?.dateAdded ?: System.currentTimeMillis()
            )

            if (id == null) {
                _addState.value = repository.addTextbook(textbook)
            } else {
                _addState.value = repository.updateTextbook(textbook)
            }
        }
    }

    fun resetState() {
        _addState.value = UiState.Empty
        _editingTextbook.value = null
        localImagePath = ""
        digitalFilePath = ""
        digitalFileType = ""
    }
}