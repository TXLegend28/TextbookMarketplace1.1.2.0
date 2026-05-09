package com.example.textbookmarketplace.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.textbookmarketplace.domain.model.AppUser
import com.example.textbookmarketplace.domain.model.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tm_settings")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val USER_ID = stringPreferencesKey("user_id")
        val USERNAME = stringPreferencesKey("username")
        val EMAIL = stringPreferencesKey("email")
        val ROLE = stringPreferencesKey("role")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    val currentUser: Flow<AppUser> = context.dataStore.data.map { prefs ->
        AppUser(
            id = prefs[USER_ID] ?: "",
            username = prefs[USERNAME] ?: "",
            email = prefs[EMAIL] ?: "",
            role = UserRole.valueOf(prefs[ROLE] ?: "BUYER"),
            isLoggedIn = prefs[IS_LOGGED_IN] ?: false
        )
    }

    val themeMode: Flow<String> = context.dataStore.data.map { it[THEME_MODE] ?: "SYSTEM" }

    // Add this:
    val isFirstLaunch: Flow<Boolean> = context.dataStore.data.map { it[FIRST_LAUNCH] ?: true }

    suspend fun saveUser(user: AppUser) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = user.id
            prefs[USERNAME] = user.username
            prefs[EMAIL] = user.email
            prefs[ROLE] = user.role.name
            prefs[IS_LOGGED_IN] = user.isLoggedIn
        }
    }

    suspend fun setTheme(mode: String) {
        context.dataStore.edit { it[THEME_MODE] = mode }
    }

    // Add this:
    suspend fun setFirstLaunchFalse() {
        context.dataStore.edit { it[FIRST_LAUNCH] = false }
    }

    suspend fun clearUser() {
        context.dataStore.edit { it.clear() }
    }
}