# 📚 TM Textbook Marketplace

A modern, offline-first Android application designed to connect students for buying, selling, and reading textbooks digitally. Built with Kotlin and Jetpack Compose, it features dual-role authentication, real-time search, in-app PDF viewing, and a polished Material 3 interface.

---

## 📑 Table of Contents

- [Group Members](#-group-members)
- [Core Features](#-core-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Installation & Setup](#-installation--setup)
- [Future Enhancements](#-future-enhancements)
- [License](#-license)

---

## 👥 Group Members

| Name | Student Number |
|------|----------------|
| Thokozani Nyingizwayo | 224028634 |
| Gareth Zuma | 223038030 |
| Someleze Ndamase | 202337430 |
| Axolile Ziyatsha | 202359774 |
| Enam Yothando Ntlonti | 202210889 |
| Asemahle Mdingi | 223031950 |

---

## ✨ Core Features

- **Dual-Role Access:** Seller/Admin vs Buyer/Reader with role-based UI rendering
- **CRUD Operations:** Add, edit, view, and delete textbook listings with cover images & PDF/DOCX attachments
- **Smart Search & Filter:** Real-time search by title, author, ISBN, or seller name; category-based filtering
- **In-App PDF Reader:** Native PDF rendering without external dependencies
- **Offline-First Architecture:** Full functionality without internet using Room database
- **Theme Support:** Light/Dark mode toggle with Material 3 gold accent (#dcc90d)
- **Data Integrity:** ISBN duplication prevention and input validation
- **Responsive UI:** Optimized for phones and tablets with smooth Compose animations

---

## 🛠️ Tech Stack

- **Language:** Kotlin 2.1.10
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM + Clean Architecture
- **Database:** Room (SQLite abstraction)
- **Dependency Injection:** Hilt
- **Async & State:** Coroutines + Kotlin Flow
- **Image Loading:** Coil
- **Preferences:** DataStore
- **Navigation:** Navigation Compose

---

## 🏗️ Architecture

The app follows a layered MVVM architecture to ensure separation of concerns, testability, and scalability:

```
┌─────────────────────────────────────────────────┐
│              PRESENTATION LAYER                 │
│ (Jetpack Compose Screens & Reusable Components) │
└───────────────────┬─────────────────────────────┘
                    │ observes StateFlow
┌───────────────────▼─────────────────────────────┐
│              VIEWMODEL LAYER                    │
│  (AuthVM, HomeVM, AddBookVM - State Management) │
└───────────────────┬─────────────────────────────┘
                    │ coordinates
┌───────────────────▼─────────────────────────────┐
│              REPOSITORY LAYER                   │
│   (Single Source of Truth - Business Logic)     │
└───────────────────┬─────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────┐
│              DATA LAYER                         │
│   ┌──────────────┐    ┌──────────────────┐      │
│   │ Room (Local) │    │ File Storage     │      │
│   │ (SQLite DB)  │    │ (Images & PDFs)  │      │
│   └──────────────┘    └──────────────────┘      │
└─────────────────────────────────────────────────┘
```
---

## 📂 Project Structure

```
app/
├── data/
│   ├── local/              # Room: AppDatabase, DAOs, UserPreferences
│   └── repository/         # TextbookRepository, ChatRepository
├── domain/
│   └── model/              # Textbook, AppUser, UiState, UserRole
├── di/                     # Hilt modules (DatabaseModule)
├── ui/
│   ├── components/         # Reusable Composables (TextbookCard, etc.)
│   ├── navigation/         # NavGraph & Screen routes
│   ├── screens/            # Login, Home, AddBook, Detail, Settings
│   ├── theme/              # Color, Theme, Type
│   └── viewmodel/          # ViewModels for each screen
└── utils/                  # FileHelper, NetworkHelper, PaymentSimulator
```

---

## 📥 Installation & Setup

1. *Prerequisites:* Android Studio Hedgehog or later, JDK 17, Android SDK 36
2. *Clone:* git clone https://github.com/TXLegend28/TextbookMarketplace1.1.2.0.git
3. *Sync:* Open project in Android Studio and allow Gradle to sync dependencies
4. *Build & Run:* ./gradlew build then launch on an emulator or physical device (API 24+)
5. *No external configuration required.* The app operates entirely on local storage.

---

## 🗺️ Future Enhancements

- *Firebase Sync:* Real-time cross-device data synchronization
- *In-App Chat:* Direct buyer-seller messaging
- *Push Notifications:* Alerts for new listings and messages
- *Payment Simulation:* End-to-end transaction flow demo
- *User Ratings & Reviews:* Reputation system for marketplace trust

---

## 📄 License

*Repository:* https://github.com/TXLegend28/TextbookMarketplace1.1.2.0  
*Course:* CSC313 - Object-Oriented Programming | Assignment 2
