# Quick Start Guide - Pocket Dictionary

## Prerequisites
- Android Studio (Ladybug or later)
- JDK 11+
- Android SDK 36

## Quick Commands

### Build & Run
```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Build and install
./gradlew clean assembleDebug installDebug
```

### Testing
```bash
# Run all unit tests
./gradlew testDebugUnitTest

# Run specific test class
./gradlew test --tests "DictionaryViewModelTest"

# Run instrumented tests (needs device/emulator)
./gradlew connectedDebugAndroidTest

# Run all tests
./gradlew test
```

### Build Outputs
- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Test Reports**: `app/build/reports/tests/testDebugUnitTest/index.html`

## Using the App

### 1. Search for a Word
1. Open the app
2. Type a word in the search box
3. Press Enter or tap search button
4. View the definition with meanings, examples, and synonyms

### 2. View Recent Searches
- Your searches are automatically saved
- Tap on any recent search to view it again
- Swipe or tap X to delete individual items
- Tap "Clear All" to remove all history

### 3. Offline Mode
- Once a word is searched, it's cached
- Works offline with previously searched words
- "Recent Searches" shows all cached words

## API Usage

**Endpoint**: https://api.dictionaryapi.dev/api/v2/entries/en/{word}

**Example**:
```bash
curl https://api.dictionaryapi.dev/api/v2/entries/en/hello
```

## Troubleshooting

### Build Issues
```bash
# Clean and rebuild
./gradlew clean build --refresh-dependencies

# Clear gradle cache
rm -rf ~/.gradle/caches/
./gradlew clean build
```

### Test Failures
```bash
# Run with stacktrace
./gradlew test --stacktrace

# View test report
open app/build/reports/tests/testDebugUnitTest/index.html
```

### Device Connection
```bash
# Check connected devices
adb devices

# Restart adb
adb kill-server
adb start-server
```

## Development Tips

### Viewing Logs
```bash
# View app logs
adb logcat | grep "PocketDictionary"

# Clear logs
adb logcat -c
```

### Database Inspection
```bash
# Pull database from device
adb pull /data/data/com.sagar.pocketdictionary/databases/dictionary_database .

# Use Room Inspector in Android Studio
# View > Tool Windows > App Inspection > Database Inspector
```

### Network Debugging
- OkHttp logging is enabled in debug builds
- Check logcat for HTTP requests/responses

## File Structure Quick Reference

```
Key Files:
├── MainActivity.kt              - Entry point
├── DictionaryScreen.kt          - Main UI
├── DictionaryViewModel.kt       - Business logic
├── DictionaryRepository.kt      - Data layer interface
├── DictionaryRepositoryImpl.kt  - Cache + API logic
├── WordDao.kt                   - Database queries
├── DictionaryApiService.kt      - API endpoints
└── WordMapper.kt                - Data transformations

Test Files:
├── DictionaryViewModelTest.kt       - ViewModel tests
├── DictionaryRepositoryImplTest.kt  - Repository tests
├── WordMapperTest.kt                - Mapper tests
├── DictionaryScreenTest.kt          - UI tests
└── MainActivityTest.kt              - E2E tests
```

## Common Tasks

### Add New Dependency
1. Edit `gradle/libs.versions.toml`
2. Add to `app/build.gradle.kts`
3. Sync Gradle

### Modify UI
- Edit `DictionaryScreen.kt` (Jetpack Compose)
- Hot reload: Ctrl+R / Cmd+R in Android Studio

### Add New Test
- Unit tests → `app/src/test/`
- UI tests → `app/src/androidTest/`
- Run: `./gradlew test`

## Performance Monitoring

### Build Time
```bash
# With build scan
./gradlew build --scan

# Profile build
./gradlew build --profile
```

### APK Size
```bash
# Analyze APK
./gradlew assembleDebug
# Open in Android Studio: Build > Analyze APK
```

## Release Build (Future)

```bash
# Generate release APK
./gradlew assembleRelease

# Generate signed bundle
./gradlew bundleRelease
```

---

**Quick Test**: `./gradlew clean testDebugUnitTest` (Should show: BUILD SUCCESSFUL, 20 tests passed)

**Quick Run**: `./gradlew installDebug` (Installs app on connected device)

