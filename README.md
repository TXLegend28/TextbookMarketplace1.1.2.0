### License

CSC313 Assignment 2 - Textbook Marketplace
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


