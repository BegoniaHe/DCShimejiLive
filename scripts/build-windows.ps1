#
# Shimeji-Live Windows Build Script
#
# This script is called by Maven to package the application for Windows.
# It creates a portable version (ZIP) and an MSI installer.
#
# Parameters:
#   $ProjectVersion   - The version of the project (e.g., "2.0.1").
#   $BuildDirectory   - The path to the Maven build directory (usually "target").
#   $BaseDirectory    - The root directory of the project.
#

param (
    [string]$ProjectVersion,
    [string]$BuildDirectory,
    [string]$BaseDirectory
)

# --- Script Setup ---
# Exit immediately if a command exits with a non-zero status.
$ErrorActionPreference = "Stop"

# Function to print a header
function Write-Header {
    param ([string]$Title)
    Write-Host "===================================================================="
    Write-Host "= $Title"
    Write-Host "===================================================================="
}

# --- Variable Definitions ---
$AppName = "Shimeji-ee"
$MainJar = "$AppName.jar"
$MainClass = "com.group_finity.mascot.Main"
$Vendor = "Shimeji-ee Group"
$IconPath = Join-Path $BaseDirectory "src/main/resources/icon.ico"

# Directories for portable build
$PortableDir = Join-Path $BuildDirectory "jpackage-windows-portable"
$PortableInputDir = Join-Path $PortableDir "input"
$PortableAppImageDir = Join-Path $PortableDir $AppName
$PortableZip = Join-Path $BuildDirectory "${AppName}_${ProjectVersion}_Windows_Portable.zip"

# Directories for MSI build
$MsiDir = Join-Path $BuildDirectory "jpackage-windows-msi"
$MsiInputDir = Join-Path $MsiDir "input"
$MsiAppImageDir = Join-Path $MsiDir $AppName

# --- Build Steps ---

# 1. Prepare Input Directories
Write-Header "Preparing input directories"
# Portable
New-Item -ItemType Directory -Force -Path $PortableInputDir
Copy-Item -Path (Join-Path $BuildDirectory $MainJar) -Destination $PortableInputDir
# MSI
New-Item -ItemType Directory -Force -Path $MsiInputDir
Copy-Item -Path (Join-Path $BuildDirectory $MainJar) -Destination $MsiInputDir
Write-Host "Input directories prepared."

# 2. Create Portable Version
Write-Header "Creating Portable Version"
if (Test-Path $PortableAppImageDir) {
    Write-Host "Cleaning old portable app image..."
    Remove-Item -Path $PortableAppImageDir -Recurse -Force
}

Write-Host "Running jpackage for portable app-image..."
jpackage --input $PortableInputDir `
    --main-jar $MainJar `
    --main-class $MainClass `
    --name $AppName `
    --app-version $ProjectVersion `
    --description "$AppName Desktop Pet Application" `
    --vendor $Vendor `
    --icon $IconPath `
    --type app-image `
    --dest $PortableDir `
    --java-options "-Xmx512M" `
    --java-options "-Xms128M" `
    --java-options "-XX:ReservedCodeCacheSize=128M" `
    --java-options "-XX:+UseZGC" `
    --java-options "--enable-native-access=ALL-UNNAMED" `
    --java-options "--add-opens=java.base/java.lang=ALL-UNNAMED" `
    --java-options "--add-opens=java.desktop/sun.awt=ALL-UNNAMED" `
    --java-options "--add-opens=java.desktop/java.awt=ALL-UNNAMED"

Write-Host "Copying resources to portable app-image..."
Copy-Item -Path (Join-Path $BaseDirectory "conf") -Destination $PortableAppImageDir -Recurse -Force
Copy-Item -Path (Join-Path $BaseDirectory "img") -Destination $PortableAppImageDir -Recurse -Force

Write-Host "Creating portable ZIP archive..."
Compress-Archive -Path $PortableAppImageDir -DestinationPath $PortableZip -Force
Write-Host "Portable version created at $PortableZip"

# 3. Create MSI Installer
Write-Header "Creating MSI Installer"
if (Test-Path $MsiAppImageDir) {
    Write-Host "Cleaning old MSI app image..."
    Remove-Item -Path $MsiAppImageDir -Recurse -Force
}

Write-Host "Running jpackage for MSI app-image..."
jpackage --input $MsiInputDir `
    --main-jar $MainJar `
    --main-class $MainClass `
    --name $AppName `
    --app-version $ProjectVersion `
    --description "$AppName Desktop Pet Application" `
    --vendor $Vendor `
    --icon $IconPath `
    --type app-image `
    --dest $MsiDir `
    --java-options "-Xmx512M" `
    --java-options "-Xms128M" `
    --java-options "-XX:ReservedCodeCacheSize=128M" `
    --java-options "-XX:+UseZGC" `
    --java-options "--enable-native-access=ALL-UNNAMED" `
    --java-options "--add-opens=java.base/java.lang=ALL-UNNAMED" `
    --java-options "--add-opens=java.desktop/sun.awt=ALL-UNNAMED" `
    --java-options "--add-opens=java.desktop/java.awt=ALL-UNNAMED"

Write-Host "Copying resources to MSI app-image..."
Copy-Item -Path (Join-Path $BaseDirectory "conf") -Destination $MsiAppImageDir -Recurse -Force
Copy-Item -Path (Join-Path $BaseDirectory "img") -Destination $MsiAppImageDir -Recurse -Force

Write-Host "Running jpackage to create MSI installer..."
jpackage --app-image $MsiAppImageDir `
    --name $AppName `
    --app-version $ProjectVersion `
    --description "$AppName Desktop Pet Application" `
    --vendor $Vendor `
    --type msi `
    --dest $MsiDir `
    --win-menu `
    --win-menu-group "Games" `
    --win-shortcut

Write-Host "Moving MSI to target directory..."
Move-Item -Path (Join-Path $MsiDir "*.msi") -Destination $BuildDirectory -Force
Write-Host "MSI installer created in $BuildDirectory"

Write-Header "Windows build process complete."