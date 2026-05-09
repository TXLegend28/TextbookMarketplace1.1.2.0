package com.example.textbookmarketplace.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.textbookmarketplace.data.local.UserPreferences
import com.example.textbookmarketplace.data.repository.TextbookRepository
import com.example.textbookmarketplace.domain.model.AppUser
import com.example.textbookmarketplace.domain.model.Textbook
import com.example.textbookmarketplace.domain.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyListingsViewModel @Inject constructor(
    private val repository: TextbookRepository,
    private val userPrefs: UserPreferences
) : ViewModel() {

    val currentUser: StateFlow<AppUser> = userPrefs.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppUser())

    val myListings: StateFlow<UiState<List<Textbook>>> = currentUser
        .flatMapLatest { user ->
            if (user.id.isNotEmpty()) repository.getMyListings(user.id)
            else flowOf(emptyList())
        }
        .map { UiState.Success(it) }
        .catch { UiState.Error(it.message ?: "Error") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    fun deleteListing(textbook: Textbook) {
        viewModelScope.launch { repository.deleteTextbook(textbook) }
    }
}