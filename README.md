# TM Marketplace v1.1.2.0

## Complete Jetpack Compose Migration with Dual-Role Auth & Offline-First Sync

### What's New in v1.1.2.0

| Feature | Status |
|---------|--------|
| **100% Jetpack Compose UI** | ✅ No XML layouts |
| **Dual-Role Login** | ✅ Seller/Admin vs Buyer/Reader |
| **Passwordless Auth** | ✅ Username + Email only |
| **Offline-First Architecture** | ✅ Room + Firebase sync |
| **Real-time Cloud Sync** | ✅ Firestore + Storage |
| **Gold Theme (#dcc90d)** | ✅ Material 3 with dark mode |
| **Image & Document Upload** | ✅ PDF/DOCX support |
| **My Listings (CRUD)** | ✅ Full seller management |
| **Web Search Integration** | ✅ Google Books redirect |
| **Contact Seller** | ✅ Direct Gmail compose |
| **Smooth Animations** | ✅ Enter/exit transitions |

### Architecture

```
app/
├── data/
│   ├── local/          # Room DAOs + DataStore
│   ├── remote/         # Firebase Firestore/Storage
│   └── repository/     # Offline-first sync logic
├── domain/
│   └── model/          # Textbook, AppUser, UiState
├── di/
│   └── DatabaseModule.kt
├── ui/
│   ├── components/     # Reusable Composables
│   ├── navigation/     # NavGraph
│   ├── screens/        # Login, Home, AddBook, Detail, MyListings, Settings
│   ├── theme/          # Color, Theme, Typography
│   └── viewmodel/      # Hilt ViewModels
└── utils/              # FileHelper, NetworkHelper
```

### Setup Instructions

1. **Create Firebase Project**
   - Go to https://console.firebase.google.com
   - Create new project "TextbookMarketplace"
   - Enable: Authentication, Firestore Database, Storage
   - Download `google-services.json` and place in `app/`

2. **Firestore Security Rules** (for development):
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /textbooks/{bookId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}
```

3. **Build & Run**
   - Open in Android Studio (Ladybug or newer)
   - Sync Gradle
   - Run on device/emulator

### User Roles

| Role | Permissions |
|------|-------------|
| **Buyer/Reader** | Browse, search, view details, read PDFs, contact sellers, web search |
| **Seller/Admin** | All buyer features + add books, edit listings, delete listings, manage inventory |

### Offline Behavior

- All data saved to Room first
- Syncs to Firebase when online
- Pending operations queued automatically
- Reads work offline with cached data
- Changes sync when connection restored

### Multi-Device Sync

Since both roles use the same Firestore database:
- Seller adds book on Phone A → syncs to cloud
- Buyer opens app on Phone B → sees new book immediately
- Works across any number of devices

### Migration from v1.1.1.0

This is a complete rewrite. The old XML-based activities are replaced with Compose screens.
Database schema upgraded (version 3) with sync flags.

### Dependencies

- Jetpack Compose BOM 2025.02.00
- Material 3
- Navigation Compose
- Room 2.6.1
- Hilt 2.55
- Coil 2.7.0
- Firebase BOM 33.10.0
- DataStore Preferences

### License

CSC313 Assignment 2 - Textbook Marketplace
