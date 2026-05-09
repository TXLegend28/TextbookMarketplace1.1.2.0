package com.example.textbookmarketplace.domain.model

data class AppUser(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val role: UserRole = UserRole.BUYER,
    val isLoggedIn: Boolean = false,
    val profileImageUrl: String = "",
    val university: String = "",
    val rating: Float = 0f,
    val listingsCount: Int = 0,
    val phoneNumber: String = ""
)