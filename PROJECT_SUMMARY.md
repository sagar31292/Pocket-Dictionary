# Pocket Dictionary - Android App

A modern Android dictionary application built with the latest Android technologies, implementing a robust caching mechanism and comprehensive testing.

## Features

✅ **Word Search**: Search for word definitions using the Free Dictionary API (https://dictionaryapi.dev)
✅ **Offline Cache**: Room database caching for offline access
✅ **Recent Searches**: View and manage your search history
✅ **Modern UI**: Material 3 Design with Jetpack Compose
✅ **Clean Architecture**: Separation of concerns with MVVM pattern
✅ **Dependency Injection**: Hilt for DI
✅ **Comprehensive Testing**: Unit tests and Espresso UI tests

## Tech Stack

### Core Technologies
- **Kotlin**: 2.0.21
- **Android Gradle Plugin**: 8.9.1
- **Minimum SDK**: 24
- **Target SDK**: 36
- **Compile SDK**: 36

### Architecture Components
- **Jetpack Compose**: Modern declarative UI framework
- **Material 3**: Latest Material Design components
- **ViewModel**: Lifecycle-aware UI state management
- **Navigation Compose**: Type-safe navigation
- **Hilt**: Dependency injection (v2.51.1)
- **Coroutines**: Asynchronous programming (v1.9.0)
- **Flow**: Reactive data streams

### Networking & Data
- **Retrofit**: HTTP client (v2.11.0)
- **Moshi**: JSON parsing (v1.15.1)
- **OkHttp**: HTTP interceptor and logging (v4.12.0)
- **Room**: Local database (v2.6.1)

### Testing
- **JUnit 4**: Unit testing framework
- **MockK**: Mocking library (v1.13.13)
- **Turbine**: Flow testing (v1.2.0)
- **Espresso**: UI testing
- **Hilt Testing**: DI testing support

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/sagar/pocketdictionary/
│   │   │   ├── data/
│   │   │   │   ├── local/          # Room database, DAOs, entities
│   │   │   │   ├── remote/         # API service, response models
│   │   │   │   ├── mapper/         # Data mappers
│   │   │   │   └── repository/     # Repository implementation
│   │   │   ├── di/                 # Dependency injection modules
│   │   │   ├── domain/             # Domain models, repository interfaces
│   │   │   ├── presentation/       # UI layer (ViewModels, Composables)
│   │   │   └── ui/theme/           # App theming
│   │   └── AndroidManifest.xml
│   ├── test/                       # Unit tests
│   └── androidTest/                # Instrumented tests
└── build.gradle.kts
```

## Architecture

The app follows **Clean Architecture** principles with clear separation of concerns:

### Layers

1. **Presentation Layer** (`presentation/`)
   - Composable UI screens
   - ViewModels for state management
   - UI state classes

2. **Domain Layer** (`domain/`)
   - Business models (Word, WordMeaning, WordDefinition)
   - Repository interfaces
   - Result sealed class for state management

3. **Data Layer** (`data/`)
   - Repository implementations
   - Data sources (Remote API, Local Database)
   - Data mappers (DTO ↔ Domain)

### Key Components

#### 1. Cache-First Strategy
```kotlin
fun getWordDefinition(word: String): Flow<Result<Word>> = flow {
    emit(Result.Loading)
    
    // Check cache first
    val cachedWord = wordDao.getWord(word)
    if (cachedWord != null) {
        emit(Result.Success(cachedWord.toDomain()))
    } else {
        // Fetch from API if not cached
        val response = apiService.getWordDefinition(word)
        // ... cache and emit result
    }
}
```

#### 2. Repository Pattern
- `DictionaryRepository` interface in domain layer
- `DictionaryRepositoryImpl` in data layer
- Abstraction for testability

#### 3. Dependency Injection
- `NetworkModule`: Retrofit, OkHttp, Moshi
- `DatabaseModule`: Room database
- `RepositoryModule`: Repository bindings

## API Integration

### Free Dictionary API
- Base URL: `https://api.dictionaryapi.dev/`
- Endpoint: `/api/v2/entries/en/{word}`
- Method: GET
- Returns: Array of word definitions with meanings, phonetics, examples, synonyms, and antonyms

### Response Structure
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
        ],
        "synonyms": ["hi", "hey"],
        "antonyms": []
      }
    ]
  }
]
```

## Database Schema

### WordEntity Table
```kotlin
@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey val word: String,
    val phonetic: String?,
    val meanings: String,  // JSON serialized
    val origin: String?,
    val timestamp: Long    // For sorting recent searches
)
```

## Testing

### Unit Tests ✅
- **DictionaryViewModelTest**: ViewModel logic testing
- **DictionaryRepositoryImplTest**: Repository logic with Flow testing
- **WordMapperTest**: Data mapping logic

### Instrumented Tests ✅
- **DictionaryScreenTest**: UI component testing
- **MainActivityTest**: End-to-end user flows
- **HiltTestRunner**: Custom test runner for Hilt

### Running Tests
```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumented tests (requires device/emulator)
./gradlew connectedDebugAndroidTest

# All tests
./gradlew test
```

## Build & Run

### Prerequisites
- Android Studio Ladybug or later
- JDK 11 or higher
- Android SDK 36

### Build Commands
```bash
# Clean build
./gradlew clean build

# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease

# Install on device
./gradlew installDebug
```

### Build Output
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Test Reports: `app/build/reports/tests/`

## Key Features Explained

### 1. Search Functionality
- Real-time search with debouncing
- Shows loading state during API calls
- Error handling with user-friendly messages
- Keyboard actions (IME) support

### 2. Caching Mechanism
- **Cache-first approach**: Check local database before API call
- **Automatic caching**: Save API responses to database
- **Offline support**: Use cached data when network fails
- **Recent searches**: Maintain search history (last 10)

### 3. UI/UX
- **Material 3 Design**: Modern, adaptive UI
- **Dark/Light theme**: System theme support
- **Responsive layout**: Works on phones and tablets
- **Edge-to-edge**: Immersive display support
- **Smooth animations**: Compose transitions

### 4. Error Handling
- Network errors with fallback to cache
- User-friendly error messages
- Dismissible error cards
- Input validation

## Configuration

### Network Timeouts
```kotlin
OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
```

### Database
- Name: `dictionary_database`
- Version: 1
- Auto-migration: Not configured (v1)

## Future Enhancements

- [ ] Audio pronunciation support
- [ ] Bookmarks/Favorites
- [ ] Word of the day
- [ ] Search suggestions
- [ ] Multiple language support
- [ ] Export/Import history
- [ ] Widget support
- [ ] Share definitions

## Known Issues & Solutions

### Issue: Hilt compatibility
**Solution**: Using Hilt 2.51.1 with AGP 8.9.1

### Issue: Flow testing complexity
**Solution**: Using Turbine library for cleaner Flow testing

### Issue: Moshi serialization in tests
**Solution**: Added KotlinJsonAdapterFactory for proper Kotlin data class support

## Contributing

When contributing to this repository:
1. Follow Kotlin coding conventions
2. Write tests for new features
3. Update documentation
4. Use conventional commits

## License

This project is created for educational purposes.

## API Attribution

Dictionary data provided by [Free Dictionary API](https://dictionaryapi.dev/)

---

**Built with ❤️ using the latest Android technologies**

