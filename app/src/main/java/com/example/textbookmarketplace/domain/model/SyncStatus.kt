package com.example.textbookmarketplace.domain.model

sealed class SyncStatus {
    object Synced : SyncStatus()
    object Pending : SyncStatus()
    object Error : SyncStatus()
    data class Syncing(val progress: Float) : SyncStatus()
}