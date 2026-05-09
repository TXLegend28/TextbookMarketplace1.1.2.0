package com.example.textbookmarketplace.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.textbookmarketplace.data.local.UserPreferences
import com.example.textbookmarketplace.domain.model.AppUser
import com.example.textbookmarketplace.domain.model.UiState
import com.example.textbookmarketplace.domain.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userPrefs: UserPreferences
) : ViewModel() {

    val currentUser: StateFlow<AppUser> = userPrefs.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppUser())

    val isLoggedIn: StateFlow<Boolean> = currentUser
        .map { it.isLoggedIn }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val themeMode: StateFlow<String> = userPrefs.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    private val _authState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val authState: StateFlow<UiState<Unit>> = _authState.asStateFlow()

    fun login(username: String, email: String, role: UserRole) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            val user = AppUser(
                id = UUID.randomUUID().toString(),
                username = username,
                email = email,
                role = role,
                isLoggedIn = true
            )
            userPrefs.saveUser(user)
            _authState.value = UiState.Success(Unit)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPrefs.clearUser()
            _authState.value = UiState.Empty
        }
    }

    fun resetState() {
        _authState.value = UiState.Empty
    }
}