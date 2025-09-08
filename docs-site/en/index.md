---
layout: home

hero:
  name: "Shimeji-Live"
  text: "A Desktop Mascot Application"
  tagline: "Let cute characters roam freely on your screen"
  actions:
    - theme: brand
      text: Quick Start
      link: /en/user/install
    - theme: alt
      text: View Tutorials
      link: /en/user/tutorial/
    - theme: alt
      text: GitHub
      link: https://github.com/DCRepairCenter/ShimejiLive

features:
  - icon: 🎮
    title: Cute Desktop Characters
    details: Let adorable characters move freely on your screen and interact with your desktop environment.
  - icon: 🎨
    title: Custom Characters and Animations
    details: Supports custom characters, animations, and behaviors to create your own unique desktop companion.
  - icon: 🖥️
    title: Multi-Monitor Support
    details: Perfect support for multi-monitor environments, allowing characters to move freely between screens.
  - icon: ⚡
    title: Modern Performance
    details: Based on Java 21 and optimized for performance, providing a smooth user experience.
  - icon: 🎯
    title: Modern Interface
    details: Features a modern user interface design that is simple and intuitive.
  - icon: 📦
    title: Multiple Installation Methods
    details: Offers a portable version, MSI installer, and JAR version to meet the needs of different users.
---
## 🚀 Quick Start

### Download and Install

Go to the [Releases](https://github.com/BegoniaHe/dc-ShimejiLive/releases) page to download the latest version:

::: code-group

```bash
# Download Shimeji-ee_x.x.x_Portable.zip
# Unzip and run Shimeji-ee.exe directly
```

```bash
# Download Shimeji-ee-x.x.x.msi
# Double-click to install and launch from the Start Menu
```

```bash
# Requires Java 21+ environment
java --enable-native-access=ALL-UNNAMED \
     --add-opens=java.base/java.lang=ALL-UNNAMED \
     --add-opens=java.desktop/sun.awt=ALL-UNNAMED \
     --add-opens=java.desktop/java.awt=ALL-UNNAMED \
     -jar Shimeji-ee.jar
```

:::

### Custom Configuration

After installation, you can modify the following files to customize your Shimeji:

- `conf/actions.xml` - Action configuration
- `conf/behaviors.xml` - Behavior configuration
- `conf/settings.properties` - Basic settings
- `img/` - Image assets folder

## 📚 Documentation Navigation

<div class="vp-doc">
  <div class="custom-block tip">
    <p class="custom-block-title">💡 Beginner's Guide</p>
    <p>If you are new to Shimeji, it is recommended to start with the <a href="/en/user/install">Installation Guide</a>, then read the <a href="/en/user/tutorial/">Tutorial Series</a>.</p>
  </div>
</div>

### User Documentation

- [📥 Installation Guide](/en/user/install) - Detailed installation and configuration instructions
- [📖 Tutorial Series](/en/user/tutorial/) - A complete tutorial from beginner to advanced

### Developer Documentation

- [⚙️ Development Guide](/en/development/getting-started) - Development environment setup and code structure
- [💡 Development Tips](/en/development/tips) - Practical development tips and best practices

## 🤝 Contributing

Contributions of code, bug reports, or suggestions are welcome!

1. Fork this project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project uses multiple licenses:

- Original Shimeji code: [zlib License](https://www.zlib.net/zlib_license.html)
- Live version extensions: [MIT License](https://opensource.org/licenses/MIT)