package com.example.textbookmarketplace.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.textbookmarketplace.data.local.UserPreferences
import com.example.textbookmarketplace.domain.model.AppUser
import com.example.textbookmarketplace.domain.model.UiState
import com.example.textbookmarketplace.domain.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userPrefs: UserPreferences
) : ViewModel() {

    val currentUser: StateFlow<AppUser> = userPrefs.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppUser())

    val isLoggedIn: StateFlow<Boolean> = currentUser.map { it.isLoggedIn }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val themeMode: StateFlow<String> = userPrefs.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    private val _authState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val authState: StateFlow<UiState<Unit>> = _authState.asStateFlow()

    fun login(username: String, email: String, role: UserRole) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            try {
                // Use Firebase Anonymous Auth to generate a real User UID without password
                val auth = FirebaseAuth.getInstance()
                val authResult = auth.signInAnonymously().await()
                val firebaseUid = authResult.user?.uid ?: "anonymous"

                val user = AppUser(
                    id = firebaseUid,
                    username = username,
                    email = email,
                    role = role,
                    isLoggedIn = true
                )

                userPrefs.saveUser(user)
                _authState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _authState.value = UiState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            // Clear local prefs
            userPrefs.clearUser()
            // Sign out of Firebase
            FirebaseAuth.getInstance().signOut()
            _authState.value = UiState.Empty
        }
    }

    fun resetState() { _authState.value = UiState.Empty }

    fun setFirstLaunchFalse() {
        viewModelScope.launch {
            userPrefs.setFirstLaunchFalse()
        }
    }
}