#!/bin/bash
#
# Shimeji-Live Unix Build Script (macOS & Linux)
#
# This script is called by Maven to package the application for macOS or Linux.
#
# Usage:
#   ./build-unix.sh <platform> <project_version> <build_dir> <base_dir>
#
# Parameters:
#   $1 - Platform: "mac" or "linux"
#   $2 - Project Version: The version of the project (e.g., "2.0.1")
#   $3 - Build Directory: The path to the Maven build directory (e.g., "target")
#   $4 - Base Directory: The root directory of the project.
#

# --- Script Setup ---
set -e # Exit immediately if a command exits with a non-zero status.

# --- Function Definitions ---
header() {
    echo "===================================================================="
    echo "= $1"
    echo "===================================================================="
}

# --- Argument Validation ---
if [ "$#" -ne 4 ]; then
    echo "Illegal number of parameters. Usage: $0 <platform> <version> <build_dir> <base_dir>"
    exit 1
fi

PLATFORM=$1
PROJECT_VERSION=$2
BUILD_DIR=$3
BASE_DIR=$4

# --- Variable Definitions ---
APP_NAME="Shimeji-ee"
MAIN_JAR="$APP_NAME.jar"
MAIN_CLASS="com.group_finity.mascot.Main"
VENDOR="Shimeji-ee Group"
ICON_PATH="$BASE_DIR/src/main/resources/icon.ico"

# Common Java options for jpackage
JAVA_OPTIONS=(
    "--java-options" "-Xmx512M"
    "--java-options" "-Xms128M"
    "--java-options" "-XX:ReservedCodeCacheSize=128M"
    "--java-options" "-XX:+UseZGC"
    "--java-options" "--enable-native-access=ALL-UNNAMED"
    "--java-options" "--add-opens=java.base/java.lang=ALL-UNNAMED"
    "--java-options" "--add-opens=java.desktop/sun.awt=ALL-UNNAMED"
    "--java-options" "--add-opens=java.desktop/java.awt=ALL-UNNAMED"
    "--java-options" "-Djava.awt.headless=false"
)

# --- Platform-Specific Logic ---

if [ "$PLATFORM" == "mac" ]; then
    # --- macOS Build ---
    header "macOS Build Started"

    # Directories
    APP_DIR="$BUILD_DIR/jpackage-macos-app"
    DMG_DIR="$BUILD_DIR/jpackage-macos-dmg"
    APP_IMAGE_NAME="$APP_NAME.app"
    PORTABLE_ZIP="$BUILD_DIR/${APP_NAME}_${PROJECT_VERSION}_macOS_Portable.zip"

    # macOS specific Java options
    PLATFORM_JAVA_OPTIONS=(
        "--java-options" "-Duser.dir=\$APPDIR"
        "--java-options" "-Dapp.dir=\$APPDIR"
        "--java-options" "-Dapple.awt.enableTemplateImages=true"
        "--java-options" "-Djava.awt.Window.locationByPlatform=false"
        "--java-options" "-Dapple.laf.useScreenMenuBar=false"
    )

    # 1. Create Portable .app
    header "Creating Portable .app"
    rm -rf "$APP_DIR" && mkdir -p "$APP_DIR/input"
    cp "$BUILD_DIR/$MAIN_JAR" "$APP_DIR/input/"

    jpackage --input "$APP_DIR/input" \
        --main-jar $MAIN_JAR \
        --main-class $MAIN_CLASS \
        --name $APP_NAME \
        --app-version "$PROJECT_VERSION" \
        --description "$APP_NAME Desktop Pet Application" \
        --vendor "$VENDOR" \
        --icon "$ICON_PATH" \
        --type app-image \
        --dest "$APP_DIR" \
        "${JAVA_OPTIONS[@]}" \
        "${PLATFORM_JAVA_OPTIONS[@]}" \
        --resource-dir "$BASE_DIR/macos-resources"

    echo "Copying resources to .app..."
    cp -r "$BASE_DIR/conf" "$APP_DIR/$APP_IMAGE_NAME/Contents/app/"
    cp -r "$BASE_DIR/img" "$APP_DIR/$APP_IMAGE_NAME/Contents/app/"

    echo "Creating portable ZIP..."
    (cd "$APP_DIR" && zip -r "$PORTABLE_ZIP" "$APP_IMAGE_NAME")
    echo "Portable version created at $PORTABLE_ZIP"

    # 2. Create Installer .dmg
    header "Creating Installer .dmg"
    rm -rf "$DMG_DIR" && mkdir -p "$DMG_DIR"
    # We can reuse the app-image we just built
    cp -r "$APP_DIR/$APP_IMAGE_NAME" "$DMG_DIR/"

    jpackage --app-image "$DMG_DIR/$APP_IMAGE_NAME" \
        --name $APP_NAME \
        --app-version "$PROJECT_VERSION" \
        --type dmg \
        --dest "$DMG_DIR"

    echo "Moving DMG to target directory..."
    find "$DMG_DIR" -name "*.dmg" -exec mv {} "$BUILD_DIR/" \;
    echo "DMG installer created in $BUILD_DIR"

    header "macOS Build Complete"

elif [ "$PLATFORM" == "linux" ]; then
    # --- Linux Build ---
    header "Linux Build Started"

    # Directories
    PORTABLE_DIR="$BUILD_DIR/jpackage-linux-portable"
    DEB_DIR="$BUILD_DIR/jpackage-linux-deb"
    RPM_DIR="$BUILD_DIR/jpackage-linux-rpm"
    PORTABLE_ZIP="$BUILD_DIR/${APP_NAME}_${PROJECT_VERSION}_Linux_Portable.zip"

    # Linux specific Java options
    PLATFORM_JAVA_OPTIONS=(
        "--java-options" "-Duser.dir=\$APPDIR"
        "--runtime-image" "$JAVA_HOME"
    )

    # 1. Create Portable Version
    header "Creating Portable Version"
    rm -rf "$PORTABLE_DIR" && mkdir -p "$PORTABLE_DIR/input"
    cp "$BUILD_DIR/$MAIN_JAR" "$PORTABLE_DIR/input/"

    jpackage --input "$PORTABLE_DIR/input" \
        --main-jar $MAIN_JAR \
        --main-class $MAIN_CLASS \
        --name $APP_NAME \
        --app-version "$PROJECT_VERSION" \
        --description "$APP_NAME Desktop Pet Application" \
        --vendor "$VENDOR" \
        --icon "$ICON_PATH" \
        --type app-image \
        --dest "$PORTABLE_DIR" \
        "${JAVA_OPTIONS[@]}" \
        "${PLATFORM_JAVA_OPTIONS[@]}"

    echo "Copying resources to portable app..."
    cp -r "$BASE_DIR/conf" "$PORTABLE_DIR/$APP_NAME/"
    cp -r "$BASE_DIR/img" "$PORTABLE_DIR/$APP_NAME/"

    echo "Creating portable ZIP..."
    (cd "$PORTABLE_DIR" && zip -r "$PORTABLE_ZIP" "$APP_NAME")
    echo "Portable version created at $PORTABLE_ZIP"

    # 2. Create .deb and .rpm installers
    for type in deb rpm; do
        header "Creating .$type Installer"
        PKG_DIR="$BUILD_DIR/jpackage-linux-$type"
        rm -rf "$PKG_DIR" && mkdir -p "$PKG_DIR"
        # We can reuse the app-image we just built
        cp -r "$PORTABLE_DIR/$APP_NAME" "$PKG_DIR/"

        jpackage_args=(
            "--app-image" "$PKG_DIR/$APP_NAME"
            "--name" "${APP_NAME,,}" # deb/rpm requires lowercase name
            "--app-version" "$PROJECT_VERSION"
            "--type" "$type"
            "--dest" "$PKG_DIR"
            "--linux-app-category" "Game"
            "--linux-shortcut"
            "--linux-menu-group" "Games"
        )
        if [ "$type" == "deb" ]; then
            jpackage_args+=("--linux-deb-maintainer" "shimeji-ee@example.com")
        else
            jpackage_args+=("--linux-rpm-license-type" "Zlib")
        fi

        jpackage "${jpackage_args[@]}"

        echo "Moving .$type to target directory..."
        find "$PKG_DIR" -name "*.$type" -exec mv {} "$BUILD_DIR/" \;
        echo ".$type installer created in $BUILD_DIR"
    done

    header "Linux Build Complete"

else
    echo "Unsupported platform: $PLATFORM. Use 'mac' or 'linux'."
    exit 1
fi