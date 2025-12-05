# Pocket Dictionary

A modern Android dictionary application that uses the [Free Dictionary API](https://dictionaryapi.dev) with offline caching capabilities.

## Features

- 🔍 Search for word definitions
- 💾 Offline caching with Room Database
- 📱 Modern Material 3 UI with Jetpack Compose
- 🔄 Automatic cache fallback when offline
- 📚 Recent search history
- ✅ Comprehensive unit and UI tests

## Architecture

The app follows **Clean Architecture** principles with MVVM pattern:

```
┌─────────────────────────────────────────────────────┐
│                 Presentation Layer                  │
│  ┌─────────────┐              ┌────────────────┐    │
│  │  Compose UI │ ◄────────────┤   ViewModel    │    │
│  └─────────────┘              └────────────────┘    │
└─────────────────────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│                  Domain Layer                       │
│  ┌────────────────┐        ┌──────────────────┐     │
│  │  Use Cases     │        │  Domain Models   │     │
│  │  (Optional)    │        │  - Word          │     │
│  └────────────────┘        │  - WordMeaning   │     │
│                            └──────────────────┘     │
└─────────────────────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│                   Data Layer                        │
│  ┌──────────────┐  ┌───────────┐  ┌────────────┐    │
│  │  Repository  │◄─┤   Mapper  │─►│  API/Cache │    │
│  └──────────────┘  └───────────┘  └────────────┘    │
│                                                     │
│  ┌──────────────┐                ┌────────────┐     │
│  │ Remote (API) │                │ Local (DB) │     │
│  │  - Retrofit  │                │  - Room    │     │
│  └──────────────┘                └────────────┘     │
└─────────────────────────────────────────────────────┘
```

## Tech Stack

### Core
- **Kotlin** - Programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material 3** - Design system
- **Coroutines & Flow** - Asynchronous programming

### Architecture Components
- **ViewModel** - UI state management
- **Room** - Local database
- **Hilt** - Dependency injection
- **Navigation Compose** - Navigation

### Networking
- **Retrofit** - HTTP client
- **Moshi** - JSON serialization
- **OkHttp** - HTTP interceptor & logging

### Testing
- **JUnit4** - Unit testing framework
- **MockK** - Mocking library
- **Espresso** - UI testing
- **Compose Test** - Compose UI testing
- **Turbine** - Flow testing utilities

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/sagar/pocketdictionary/
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── dao/           # Room DAOs
│   │   │   │   │   ├── entity/        # Room entities
│   │   │   │   │   └── DictionaryDatabase.kt
│   │   │   │   ├── remote/
│   │   │   │   │   ├── model/         # API response models
│   │   │   │   │   └── DictionaryApiService.kt
│   │   │   │   ├── repository/        # Repository implementations
│   │   │   │   └── mapper/            # Data mappers
│   │   │   ├── domain/
│   │   │   │   ├── model/             # Domain models
│   │   │   │   └── repository/        # Repository interfaces
│   │   │   ├── presentation/
│   │   │   │   └── dictionary/        # UI & ViewModels
│   │   │   ├── di/                    # Hilt modules
│   │   │   ├── MainActivity.kt
│   │   │   └── PocketDictionaryApp.kt
│   │   └── AndroidManifest.xml
│   ├── test/                          # Unit tests
│   │   └── java/com/sagar/pocketdictionary/
│   │       ├── data/
│   │       │   ├── repository/
│   │       │   └── mapper/
│   │       └── presentation/
│   └── androidTest/                   # Instrumentation tests
│       └── java/com/sagar/pocketdictionary/
│           ├── presentation/
│           ├── MainActivityTest.kt
│           └── HiltTestRunner.kt
└── build.gradle.kts
```

## Key Features Implementation

### 1. Caching Mechanism
The app implements a **cache-first strategy**:
1. Check local Room database for cached word
2. If found, return immediately
3. If not found, fetch from API
4. Cache the result for future use
5. On API failure, fallback to cache if available

```kotlin
suspend fun getWordDefinition(word: String): Flow<Result<Word>> = flow {
    emit(Result.Loading)
    
    // Check cache first
    val cachedWord = wordDao.getWord(word.lowercase())
    if (cachedWord != null) {
        emit(Result.Success(cachedWord.toDomain()))
        return@flow
    }
    
    // Fetch from API
    try {
        val response = apiService.getWordDefinition(word.lowercase())
        // Cache and return
    } catch (e: Exception) {
        // Fallback to cache
    }
}
```

### 2. Dependency Injection with Hilt
Three main modules:
- **DatabaseModule** - Provides Room Database and DAOs
- **NetworkModule** - Provides Retrofit, OkHttp, Moshi
- **RepositoryModule** - Binds repository implementations

### 3. Modern UI with Compose
- Material 3 design
- Reactive state management with StateFlow
- Composable screens with proper state hoisting
- Keyboard actions for better UX

### 4. Comprehensive Testing

#### Unit Tests
- Repository layer (with MockK)
- ViewModel layer (with test coroutines)
- Data mapper tests

#### Instrumentation Tests
- Compose UI tests
- End-to-end user flow tests
- Hilt integration tests

## Building & Running

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11 or later
- Android SDK 24+

### Build
```bash
./gradlew assembleDebug
```

### Run Tests
```bash
# Unit tests
./gradlew test

# Instrumentation tests
./gradlew connectedAndroidTest
```

### Install
```bash
./gradlew installDebug
```

## API Reference

This app uses the [Free Dictionary API](https://dictionaryapi.dev/)

**Endpoint:**
```
GET https://api.dictionaryapi.dev/api/v2/entries/en/{word}
```

**Example Response:**
```json
[
  {
    "word": "hello",
    "phonetic": "/həˈloʊ/",
    "meanings": [
      {
        "partOfSpeech": "noun",
        "definitions": [
          {
            "definition": "A greeting",
            "example": "Hello world"
          }
        ]
      }
    ]
  }
]
```

## Future Enhancements

- [ ] Audio pronunciation playback
- [ ] Bookmarks/Favorites
- [ ] Dark mode support
- [ ] Search suggestions
- [ ] Multiple language support
- [ ] Offline mode indicator
- [ ] Share definitions
- [ ] Search history with timestamps

## License

MIT License - Feel free to use this project for learning purposes.

## Author

Sagar - Android Developer

