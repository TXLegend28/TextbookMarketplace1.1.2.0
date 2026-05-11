### Group Work

CSC313 Assignment 2 - Textbook Marketplace

### Group Members
1. Thokozani Nyingizwayo - 224028634
2. Gareth Zuma - 223038030
3. Someleze Ndamase - 202337430
4. Axolile Ziyatsha - 202359774
5. Enam Yothando Ntlonti- 202210889
6. Asemahle Mdingi - 223031950

### Code Organization
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


