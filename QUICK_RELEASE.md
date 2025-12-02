# 🚀 Quick Release Guide

## TL;DR - Release to Play Store in 3 Steps

### 1️⃣ Bump Version
```bash
./bump-version.sh patch  # or 'minor' or 'major'
```

### 2️⃣ Push Tag
```bash
git push origin main --tags
```

### 3️⃣ Done! ✨
GitHub Actions will automatically:
- Build signed AAB/APK
- Upload to Play Store (internal track)
- Create GitHub release

---

## 📝 Release Checklist

Before releasing:

- [ ] Test app thoroughly on multiple devices
- [ ] Update release notes in `distribution/whatsnew/whatsnew-en-US`
- [ ] Bump version using `./bump-version.sh`
- [ ] Verify all tests pass: `./gradlew test`
- [ ] Check lint: `./gradlew lint`
- [ ] Review ProGuard rules if needed
- [ ] Push tag to trigger release

---

## 🎯 Quick Commands

### Version Management
```bash
# Bump patch version (1.0.0 → 1.0.1)
./bump-version.sh patch

# Bump minor version (1.0.1 → 1.1.0)
./bump-version.sh minor

# Bump major version (1.1.0 → 2.0.0)
./bump-version.sh major
```

### Manual Build & Test
```bash
# Build debug APK
./gradlew assembleDebug

# Build release AAB (requires keystore setup)
./gradlew bundleRelease

# Run tests
./gradlew test

# Run lint checks
./gradlew lint
```

### Git Commands
```bash
# Create and push tag manually
git tag v1.0.0
git push origin v1.0.0

# List all tags
git tag -l

# Delete a tag (local and remote)
git tag -d v1.0.0
git push origin :refs/tags/v1.0.0
```

---

## 🎭 Testing Tracks

| Track | Purpose | Review Time | Testers |
|-------|---------|-------------|---------|
| **Internal** | Team testing | Minutes | Up to 100 |
| **Alpha** | Early adopters | Hours | Unlimited |
| **Beta** | Public testing | Hours | Unlimited |
| **Production** | Public release | 1-7 days | Everyone |

---

## 🔧 Troubleshooting

### "Build failed: Keystore not found"
1. Ensure GitHub secrets are set: `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`
2. Verify service account JSON is set: `PLAY_STORE_SERVICE_ACCOUNT_JSON`

### "Version code must be greater than the current version code"
1. Increment version code in `app/build.gradle.kts`
2. Use `./bump-version.sh` script to avoid this

### "Package name mismatch"
1. Verify `applicationId` in `build.gradle.kts` matches Play Console

---

## 📊 Workflow Status

View workflow runs: 
- Go to **Actions** tab in GitHub
- Check **Android Release to Play Store** workflow

---

## 🎉 Manual Release Trigger

Can't use tags? Trigger manually:

1. Go to GitHub → **Actions** tab
2. Select **"Android Release to Play Store"**
3. Click **"Run workflow"**
4. Choose track (internal/alpha/beta/production)
5. Click **"Run workflow"** button

---

## 📚 Full Documentation

See `PLAY_STORE_SETUP.md` for complete setup instructions.

---

**Happy shipping! 🚀**

