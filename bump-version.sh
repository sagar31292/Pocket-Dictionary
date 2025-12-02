#!/bin/bash

# Script to bump version and create release tag
# Usage: ./bump-version.sh [major|minor|patch]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Get current version from build.gradle.kts
BUILD_GRADLE="app/build.gradle.kts"

if [ ! -f "$BUILD_GRADLE" ]; then
    echo -e "${RED}Error: $BUILD_GRADLE not found${NC}"
    exit 1
fi

# Extract current version
CURRENT_VERSION_CODE=$(grep "versionCode = " "$BUILD_GRADLE" | sed 's/.*versionCode = //' | sed 's/ .*//')
CURRENT_VERSION_NAME=$(grep "versionName = " "$BUILD_GRADLE" | sed 's/.*versionName = "//' | sed 's/".*//')

echo -e "${YELLOW}Current version: $CURRENT_VERSION_NAME (code: $CURRENT_VERSION_CODE)${NC}"

# Parse version components
IFS='.' read -r -a VERSION_PARTS <<< "$CURRENT_VERSION_NAME"
MAJOR="${VERSION_PARTS[0]}"
MINOR="${VERSION_PARTS[1]}"
PATCH="${VERSION_PARTS[2]}"

# Determine bump type
BUMP_TYPE="${1:-patch}"

case $BUMP_TYPE in
    major)
        MAJOR=$((MAJOR + 1))
        MINOR=0
        PATCH=0
        ;;
    minor)
        MINOR=$((MINOR + 1))
        PATCH=0
        ;;
    patch)
        PATCH=$((PATCH + 1))
        ;;
    *)
        echo -e "${RED}Error: Invalid bump type. Use: major, minor, or patch${NC}"
        exit 1
        ;;
esac

NEW_VERSION_NAME="$MAJOR.$MINOR.$PATCH"
NEW_VERSION_CODE=$((CURRENT_VERSION_CODE + 1))

echo -e "${GREEN}New version: $NEW_VERSION_NAME (code: $NEW_VERSION_CODE)${NC}"

# Ask for confirmation
read -p "Proceed with version bump? (y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}Version bump cancelled${NC}"
    exit 0
fi

# Update build.gradle.kts
echo "Updating $BUILD_GRADLE..."
sed -i.bak "s/versionCode = $CURRENT_VERSION_CODE/versionCode = $NEW_VERSION_CODE/" "$BUILD_GRADLE"
sed -i.bak "s/versionName = \"$CURRENT_VERSION_NAME\"/versionName = \"$NEW_VERSION_NAME\"/" "$BUILD_GRADLE"
rm "${BUILD_GRADLE}.bak"

echo -e "${GREEN}✓ Version updated in $BUILD_GRADLE${NC}"

# Ask if user wants to commit and tag
read -p "Create git commit and tag v$NEW_VERSION_NAME? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    # Check if there are uncommitted changes
    if ! git diff-index --quiet HEAD --; then
        echo -e "${YELLOW}Warning: You have uncommitted changes. Commit them first.${NC}"
        read -p "Stage all changes and commit? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            git add -A
        else
            echo -e "${YELLOW}Please commit your changes manually, then run:${NC}"
            echo "  git tag v$NEW_VERSION_NAME"
            echo "  git push origin main --tags"
            exit 0
        fi
    fi

    # Commit the version bump
    git add "$BUILD_GRADLE"
    git commit -m "Bump version to $NEW_VERSION_NAME"

    # Create tag
    git tag -a "v$NEW_VERSION_NAME" -m "Release version $NEW_VERSION_NAME"

    echo -e "${GREEN}✓ Commit and tag created${NC}"
    echo -e "${YELLOW}To push to GitHub and trigger release, run:${NC}"
    echo "  git push origin main"
    echo "  git push origin v$NEW_VERSION_NAME"
    echo ""
    echo -e "${GREEN}Or push both at once:${NC}"
    echo "  git push origin main --tags"
else
    echo -e "${YELLOW}Remember to commit and tag manually:${NC}"
    echo "  git add $BUILD_GRADLE"
    echo "  git commit -m 'Bump version to $NEW_VERSION_NAME'"
    echo "  git tag v$NEW_VERSION_NAME"
    echo "  git push origin main --tags"
fi

echo ""
echo -e "${GREEN}✓ Version bump complete!${NC}"

