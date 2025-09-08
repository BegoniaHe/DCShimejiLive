# Development Guide

Welcome to the Shimeji-Live development documentation! This guide will help you understand the project structure, set up your development environment, and start contributing code.

## Project Overview

Shimeji-Live is a Java-based desktop mascot application that lets cute characters roam freely on the user's screen. The project uses a modern Java technology stack and supports cross-platform operation.

### Technology Stack

- **Java 21+** - Main programming language
- **Maven** - Project build tool
- **Swing** - GUI framework
- **JNA** - Native system calls
- **FlatLaf** - Modern look and feel theme

## Quick Start

### Environment Requirements

- Java 21 or higher
- Maven 3.8+
- Git
- IDE (IntelliJ IDEA or Eclipse recommended)

### Clone and Build

```bash
# Clone the repository
git clone https://github.com/DCRepairCenter/ShimejiLive.git
cd ShimejiLive

# Compile the project
mvn clean compile

# Run the application
mvn -P run

# Package the application
mvn clean package
```

### Running in Development Mode

```bash
# Run directly with Maven
mvn exec:java -Dexec.mainClass="com.group_finity.mascot.Main"

# Or use the predefined profile
mvn -P run
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/group_finity/mascot/
│   │       ├── Main.java              # Main entry point
│   │       ├── Manager.java           # Mascot manager
│   │       ├── Mascot.java            # Mascot class
│   │       ├── action/                # Action implementations
│   │       ├── behavior/              # Behavior logic
│   │       ├── config/                # Configuration parsing
│   │       ├── environment/           # Environment detection
│   │       ├── image/                 # Image processing
│   │       └── win/                   # Windows-specific implementation
│   └── resources/                     # Resource files
conf/                                  # Configuration files
img/                                   # Image assets
docs/                                  # Documentation
target/                                # Build output
```

## Core Concepts

### 1. Mascot
The `Mascot` class is the core entity, representing a single character instance on the screen. Each mascot has its own:
- Position and velocity
- Current behavior state
- Display window
- Animation sequence

### 2. Behavior
Behaviors define the action patterns of a mascot, defined through XML configuration files:
- `behaviors.xml` - List of behaviors and their trigger conditions
- `actions.xml` - Specific action implementations

### 3. Environment
The environment system detects the state of the desktop:
- Screen boundaries
- Active windows
- Mouse position

## Debugging Tips

### Enable Debug Mode

In `conf/settings.properties`, set:
```properties
DebugMode=true
ShowDebugWindow=true
```

### View Logs

Log files are located in the application directory:
```bash
tail -f shimeji.log
```

### Performance Analysis

Use a tool like JProfiler:
```bash
java -javaagent:jprofiler.jar -jar Shimeji-ee.jar
```

## Contribution Guide

### Code Style

- Use 4 spaces for indentation
- Class names use PascalCase
- Method names use camelCase
- Constants use UPPER_SNAKE_CASE

### Commit Convention

```bash
# Feature addition
git commit -m "feat: Add new action type"

# Bug fix
git commit -m "fix: Fix position calculation issue in multi-monitor environment"

# Documentation update
git commit -m "docs: Update development guide"
```

### Pull Request Process

1. Fork the project to your account
2. Create a feature branch: `git checkout -b feature/new-feature`
3. Commit your changes: `git commit -am 'Add new feature'`
4. Push the branch: `git push origin feature/new-feature`
5. Create a Pull Request

## FAQ

### Q: How do I add a new action?

A: Refer to the [Action System documentation](/en/development/actions) for detailed steps.

### Q: How do I support a new operating system?

A: You need to implement the `NativeFactory` interface, referencing the existing Windows implementation.

### Q: How do I debug configuration parsing issues?

A: Enable debug mode and check the console output for XML parsing logs.

## Related Documents

- [Action System Details](/en/development/actions)
- [Configuration File Format](/en/development/configuration)
- [Build and Deploy](/en/development/build-deploy)

---

If you have any questions, feel free to open an issue on [GitHub Issues](https://github.com/DCRepairCenter/ShimejiLive/issues).