# GitHub Actions CI/CD Configuration

## Overview

This project includes two GitHub Actions workflows for automated building, testing, and deployment.

## Workflows

### 1. 🔄 Continuous Integration (`ci.yml`)

**Triggers:** 
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop` branches

**Actions:**
- ✅ Lint checks
- ✅ Unit tests  
- ✅ Build debug APK
- ✅ Upload artifacts

### 2. 🚀 Release to Play Store (`release.yml`)

**Triggers:**
- Git tags starting with `v` (e.g., `v1.0.0`)
- Manual workflow dispatch

**Actions:**
- ✅ Build signed release AAB
- ✅ Build signed release APK
- ✅ Upload to Google Play Store
- ✅ Create GitHub release
- ✅ Upload ProGuard mapping files

## Required GitHub Secrets

Configure these in **Settings → Secrets and variables → Actions**:

| Secret Name | Description | How to Get |
|-------------|-------------|------------|
| `KEYSTORE_BASE64` | Base64-encoded keystore file | `base64 -i keystore.jks \| pbcopy` |
| `KEYSTORE_PASSWORD` | Keystore password | From keystore creation |
| `KEY_ALIAS` | Key alias name | From keystore creation |
| `KEY_PASSWORD` | Key password | From keystore creation |
| `PLAY_STORE_SERVICE_ACCOUNT_JSON` | Service account JSON | Play Console → API access |

## Setup Instructions

### Step 1: Generate Keystore (First Time Only)

```bash
keytool -genkey -v -keystore pocket-dictionary.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias pocket-dictionary-key
```

**Important:** Backup this file securely! You cannot update your app without it.

### Step 2: Encode Keystore to Base64

**macOS/Linux:**
```bash
base64 -i pocket-dictionary.jks | pbcopy
```

**Windows (PowerShell):**
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("pocket-dictionary.jks")) | Set-Clipboard
```

### Step 3: Create Play Store Service Account

1. Go to [Google Play Console](https://play.google.com/console)
2. Navigate to **Setup → API access**
3. Click **Create new service account**
4. Follow to Google Cloud Console
5. Create service account: `github-actions-play-store`
6. Download JSON key file
7. Grant permissions in Play Console:
   - **Releases → Edit releases**
   - **Releases → Manage testing tracks**

### Step 4: Add Secrets to GitHub

1. Go to your repository on GitHub
2. Click **Settings → Secrets and variables → Actions**
3. Click **New repository secret**
4. Add all five secrets listed above

### Step 5: Test the Setup

**Option A: Using Git Tags**
```bash
# Update version in app/build.gradle.kts
./bump-version.sh patch

# Push tag
git push origin main --tags
```

**Option B: Manual Trigger**
1. Go to **Actions** tab
2. Select **"Android Release to Play Store"**
3. Click **"Run workflow"**
4. Choose track: `internal`
5. Click **"Run workflow"**

## Release Process

### Automatic Release (Recommended)

1. **Develop and test** your changes
2. **Bump version**:
   ```bash
   ./bump-version.sh patch  # or minor, or major
   ```
3. **Push tag**:
   ```bash
   git push origin main --tags
   ```
4. **Wait for GitHub Actions** to complete (5-10 minutes)
5. **Test on Play Store** internal track
6. **Promote to production** when ready

### Manual Release

1. Go to **Actions** tab on GitHub
2. Select **"Android Release to Play Store"** workflow
3. Click **"Run workflow"**
4. Choose target track (internal/alpha/beta/production)
5. Click **"Run workflow"**

## Play Store Testing Tracks

| Track | Purpose | Review | Testers | Rollout |
|-------|---------|--------|---------|---------|
| **Internal** | Quick team testing | None | 100 | Minutes |
| **Alpha** | Early testers | Initial | Unlimited | Hours |
| **Beta** | Public testing | Initial | Unlimited | Hours |
| **Production** | Public release | Full | Everyone | 1-7 days |

## Version Management

### Semantic Versioning

Format: `MAJOR.MINOR.PATCH`

- **MAJOR**: Breaking changes (1.0.0 → 2.0.0)
- **MINOR**: New features, backward compatible (1.0.0 → 1.1.0)
- **PATCH**: Bug fixes (1.0.0 → 1.0.1)

### Version Code

Must increment for each release:
```kotlin
versionCode = 1  // Increment: 1, 2, 3, 4...
versionName = "1.0.0"  // Semantic version
```

Use the helper script:
```bash
./bump-version.sh patch  # Auto-increments both
```

## Troubleshooting

### "Keystore not found"
- Check `KEYSTORE_BASE64` secret is set correctly
- Verify base64 encoding is proper (no line breaks)

### "Permission denied" on Play Store upload
- Verify service account has correct permissions
- Check JSON is valid in `PLAY_STORE_SERVICE_ACCOUNT_JSON`

### "Version code already exists"
- Increment `versionCode` in `build.gradle.kts`
- Version codes cannot be reused

### "Package name mismatch"
- Verify `applicationId` matches Play Console
- Package name is set on first upload and cannot change

## Monitoring

### View Workflow Runs
- Go to **Actions** tab in GitHub
- Click on a workflow run to see logs
- Download artifacts (APK, AAB, mapping files)

### Check Build Status
Workflow status badges can be added to README:

```markdown
![CI](https://github.com/username/repo/workflows/Android%20CI/badge.svg)
![Release](https://github.com/username/repo/workflows/Android%20Release/badge.svg)
```

## Security Best Practices

- ✅ Never commit keystore files (they're in `.gitignore`)
- ✅ Never commit `keystore.properties` (also in `.gitignore`)
- ✅ Use strong passwords for keystore
- ✅ Backup keystore in secure, encrypted location
- ✅ Rotate service account keys periodically
- ✅ Use separate debug/release signing configs

## Local Development

For local release builds, create `keystore.properties`:

```properties
storeFile=/path/to/keystore.jks
storePassword=your_password
keyAlias=your_alias
keyPassword=your_key_password
```

Then build:
```bash
./gradlew bundleRelease
```

## Useful Commands

```bash
# Build debug
./gradlew assembleDebug

# Build release (requires keystore)
./gradlew bundleRelease

# Run tests
./gradlew test

# Run lint
./gradlew lint

# Clean build
./gradlew clean

# Bump version and create tag
./bump-version.sh patch
```

## File Structure

```
.github/
├── workflows/
│   ├── ci.yml              # Continuous integration
│   └── release.yml         # Play Store release
distribution/
└── whatsnew/
    └── whatsnew-en-US      # Release notes
app/
├── build.gradle.kts        # Build config with signing
└── proguard-rules.pro      # ProGuard rules
bump-version.sh             # Version bump helper
keystore.properties.example # Keystore config template
```

## Documentation

- 📚 [Complete Setup Guide](PLAY_STORE_SETUP.md)
- 🚀 [Quick Release Guide](QUICK_RELEASE.md)
- 📱 [App Icon Documentation](APP_ICON.md)

## Support

For issues with:
- **GitHub Actions**: Check workflow logs in Actions tab
- **Play Store**: Check [Play Console](https://play.google.com/console)
- **Build issues**: Run `./gradlew --stacktrace`

---

**Ready to ship! 🚀**

