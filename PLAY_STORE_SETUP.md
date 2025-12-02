# GitHub Actions Setup for Google Play Store Publishing

This guide explains how to set up automated publishing to Google Play Store using GitHub Actions.

## 📋 Prerequisites

Before setting up the workflow, you need:

1. **Google Play Developer Account** ($25 one-time fee)
2. **App created in Google Play Console**
3. **Signing keystore** for your app
4. **Service Account** with Play Store publishing permissions

---

## 🔐 Step 1: Generate a Signing Keystore

If you don't have a keystore yet, generate one:

```bash
keytool -genkey -v -keystore pocket-dictionary.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias pocket-dictionary-key
```

**Important:** Save the keystore file and passwords securely. If you lose them, you cannot update your app!

---

## 🔑 Step 2: Create Google Play Service Account

1. Go to [Google Play Console](https://play.google.com/console)
2. Navigate to **Setup → API access**
3. Click **Create new service account**
4. Follow the link to Google Cloud Console
5. Create a service account with these details:
   - Name: `github-actions-play-store`
   - Role: `Service Account User`
6. Create and download the JSON key file
7. Back in Play Console, grant the service account these permissions:
   - **Releases → Edit releases**
   - **Releases → Manage testing tracks**

---

## 🔧 Step 3: Configure GitHub Secrets

Go to your GitHub repository → **Settings → Secrets and variables → Actions**

Add these secrets:

### Required Secrets:

1. **`KEYSTORE_BASE64`**
   ```bash
   # Encode your keystore to base64
   base64 -i pocket-dictionary.jks | pbcopy  # macOS
   # or
   base64 pocket-dictionary.jks | xclip -selection clipboard  # Linux
   ```
   Paste the output as the secret value.

2. **`KEYSTORE_PASSWORD`**
   - The password you used when creating the keystore

3. **`KEY_ALIAS`**
   - The alias you specified (e.g., `pocket-dictionary-key`)

4. **`KEY_PASSWORD`**
   - The key password (might be same as keystore password)

5. **`PLAY_STORE_SERVICE_ACCOUNT_JSON`**
   - Copy the entire content of the service account JSON file
   - Paste it as-is (the full JSON)

---

## 📦 Step 4: Prepare Your App for Release

### Update Version Information

Edit `app/build.gradle.kts`:

```kotlin
defaultConfig {
    applicationId = "com.sagar.pocketdictionary"
    versionCode = 1  // Increment for each release
    versionName = "1.0.0"  // Semantic versioning
}
```

### Update Release Notes

Edit `distribution/whatsnew/whatsnew-en-US`:

```
- Initial release of Pocket Dictionary
- Search for word definitions
- Save favorite words
- Material Design 3 UI
```

For other languages, create files like:
- `whatsnew-es-ES` (Spanish)
- `whatsnew-fr-FR` (French)
- `whatsnew-de-DE` (German)

---

## 🚀 Step 5: Deploy to Play Store

### Option 1: Automatic Deployment with Git Tags

```bash
# Create and push a version tag
git tag v1.0.0
git push origin v1.0.0
```

This automatically:
1. Builds a signed release AAB
2. Uploads to Play Store (internal track)
3. Creates a GitHub release with APK/AAB artifacts

### Option 2: Manual Deployment via GitHub UI

1. Go to **Actions** tab in your repository
2. Select **"Android Release to Play Store"** workflow
3. Click **"Run workflow"**
4. Choose the track:
   - **internal**: For internal testing (up to 100 testers)
   - **alpha**: For alpha testing
   - **beta**: For beta testing (open or closed)
   - **production**: For public release
5. Click **"Run workflow"**

---

## 📱 Play Store Testing Tracks

### Internal Testing Track
- Fastest deployment (usually within minutes)
- Up to 100 internal testers
- Good for team testing
- No review required

### Alpha/Beta Testing Tracks
- Can have unlimited testers
- Requires initial app review
- Use for wider testing

### Production Track
- Public release
- Requires full app review
- Can take hours to days for approval

---

## 🔄 Release Process Workflow

```
1. Develop feature → 2. Test locally → 3. Push to main
                                              ↓
4. Create git tag → 5. GitHub Actions triggered → 6. Build signed AAB
                                                           ↓
7. Upload to Play Store (internal) → 8. Test on real devices
                                              ↓
9. Promote to beta/production → 10. Users receive update
```

---

## 📋 Version Management

### Semantic Versioning (Recommended)

- **Major.Minor.Patch** (e.g., 1.2.3)
- **Major**: Breaking changes
- **Minor**: New features (backward compatible)
- **Patch**: Bug fixes

### Version Code

```kotlin
versionCode = 1  // Must increment for each release
versionName = "1.0.0"
```

**Important:** `versionCode` must be higher than the previous release!

---

## 🔍 Troubleshooting

### Build Fails: "Keystore not found"
- Verify `KEYSTORE_BASE64` secret is set correctly
- Check that base64 encoding was done properly

### Upload Fails: "Permission denied"
- Verify service account has correct permissions
- Check `PLAY_STORE_SERVICE_ACCOUNT_JSON` is valid JSON

### "Version code already exists"
- Increment `versionCode` in `build.gradle.kts`
- You cannot reuse version codes

### "Package name mismatch"
- Verify `applicationId` matches the one in Play Console
- Package name cannot be changed after first upload

---

## 🔒 Security Best Practices

1. **Never commit keystore files** - They're in `.gitignore`
2. **Never commit keystore.properties** - Also in `.gitignore`
3. **Use strong passwords** for keystore
4. **Backup your keystore** securely (encrypted cloud storage)
5. **Rotate service account keys** periodically
6. **Use separate keystores** for debug and release builds

---

## 📊 CI/CD Workflow Features

### Automated CI (on every push/PR)
✅ Linting checks  
✅ Unit tests  
✅ Build debug APK  
✅ Upload test reports  

### Automated Release (on version tags)
✅ Build signed release AAB  
✅ Build signed release APK  
✅ Upload to Play Store  
✅ Create GitHub release  
✅ Upload ProGuard mapping files  

---

## 🎯 Quick Commands

```bash
# Check current version
grep "versionCode\|versionName" app/build.gradle.kts

# Increment version and tag
# Edit build.gradle.kts first, then:
git add app/build.gradle.kts
git commit -m "Bump version to 1.0.1"
git tag v1.0.1
git push origin main --tags

# Manual build and sign
./gradlew bundleRelease

# Check AAB output
ls -lh app/build/outputs/bundle/release/
```

---

## 📚 Additional Resources

- [Google Play Console Help](https://support.google.com/googleplay/android-developer)
- [Android App Signing](https://developer.android.com/studio/publish/app-signing)
- [GitHub Actions for Android](https://github.com/actions/setup-java)
- [Play Store Publishing API](https://developers.google.com/android-publisher)

---

## ✅ Checklist Before First Release

- [ ] App created in Play Console
- [ ] Keystore generated and backed up
- [ ] Service account created with correct permissions
- [ ] All GitHub secrets configured
- [ ] App screenshots and store listing completed in Play Console
- [ ] Privacy policy URL added (if collecting user data)
- [ ] Content rating questionnaire completed
- [ ] Target audience and content settings configured
- [ ] Version code and name set correctly
- [ ] Release notes written
- [ ] App tested thoroughly on multiple devices

---

## 🎉 Success!

Once set up, your deployment process is:

1. Develop and test
2. Update version in `build.gradle.kts`
3. Create and push a git tag
4. Wait for GitHub Actions to complete
5. Test on Play Store internal track
6. Promote to production when ready

**Happy shipping! 🚀**

