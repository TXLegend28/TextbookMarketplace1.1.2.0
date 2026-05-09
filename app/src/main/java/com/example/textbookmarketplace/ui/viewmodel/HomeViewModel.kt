package com.example.textbookmarketplace.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.textbookmarketplace.data.local.UserPreferences
import com.example.textbookmarketplace.data.repository.TextbookRepository
import com.example.textbookmarketplace.domain.model.AppUser
import com.example.textbookmarketplace.domain.model.Textbook
import com.example.textbookmarketplace.domain.model.UiState
import com.example.textbookmarketplace.domain.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TextbookRepository,
    private val userPrefs: UserPreferences
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val currentUser: StateFlow<AppUser> = userPrefs.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppUser())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val textbooks: StateFlow<UiState<List<Textbook>>> = combine(
        _searchQuery, _selectedCategory
    ) { query, category ->
        Pair(query, category)
    }.flatMapLatest { (query, category) ->
        when {
            query.isNotBlank() && query.matches(Regex("^\\d{10,13}$")) -> {
                // ISBN Search - getTextbookByIsbn is suspend, so we wrap it in a flow
                flow {
                    val book = repository.getTextbookByIsbn(query)
                    emit(if (book != null) listOf(book) else emptyList())
                }
            }
            query.isNotBlank() -> repository.searchTextbooks(query)
            category != "All" -> repository.getTextbooksByCategory(category)
            else -> repository.getAllTextbooks()
        }
    }.map { UiState.Success(it) as UiState<List<Textbook>> }
        .catch { emit(UiState.Error(it.message ?: "Error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setCategory(category: String) { _selectedCategory.value = category }

    val isSeller: StateFlow<Boolean> = currentUser.map { it.role == UserRole.SELLER || it.role == UserRole.ADMIN }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    suspend fun getTextbookById(id: String): Textbook? = repository.getTextbookById(id)
}