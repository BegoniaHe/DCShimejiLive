# Installation Guide

## System Requirements

- Windows 10/11 (64-bit)
- Java 21+ (if using the JAR version)

## Installation Methods

### Method 1: Portable Version (Recommended)

1. Download `Shimeji-ee_x.x.x_Portable.zip`
2. Extract to any directory
3. Run `Shimeji-ee.exe`

### Method 2: MSI Installer

1. Download `Shimeji-ee-x.x.x.msi`
2. Double-click to run the installer
3. Follow the wizard to complete the installation
4. Launch the application from the Start Menu

### Method 3: JAR Version

1. Ensure you have Java 21+ installed
2. Download `Shimeji-ee.jar`
3. Run from the command line:

```bash
java --enable-native-access=ALL-UNNAMED \
     --add-opens=java.base/java.lang=ALL-UNNAMED \
     --add-opens=java.desktop/sun.awt=ALL-UNNAMED \
     --add-opens=java.desktop/java.awt=ALL-UNNAMED \
     -jar Shimeji-ee.jar
```

## Custom Configuration

After installation, you can modify the following files to customize your Shimeji:

- `conf/actions.xml` - Action configuration
- `conf/behaviors.xml` - Behavior configuration
- `conf/settings.properties` - Basic settings
- `img/` - Image assets folder

## FAQ

### Fails to Start

- Ensure you have Java 21+ installed
- Check if antivirus software is blocking the application

### Performance Issues

- Reduce the number of simultaneously running Shimeji
- Disable unnecessary animation effects

### Custom Characters

Please refer to the detailed instructions in the [Tutorial Documents](/en/user/tutorial/).

## Download Link

Go to the [GitHub Releases](https://github.com/DCRepairCenter/ShimejiLive/releases) page to download the latest version.