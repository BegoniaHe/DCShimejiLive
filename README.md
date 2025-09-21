# Shimeji-Live

一个桌面吉祥物应用程序，让可爱的角色在您的屏幕上自由活动 - Live版本

## ✨ 特性

- 🎮 可爱的桌面角色自由在屏幕上活动
- 🎨 支持自定义角色和动画
- 🖥️ 支持多显示器环境
- ⚡ 基于Java 21，性能优化
- 🎯 现代化的用户界面
- 📦 提供多种安装方式

## 🚀 快速开始

### 下载安装

前往 [Releases](https://github.com/BegoniaHe/dc-ShimejiLive/releases) 页面下载最新版本：

- **便携版** (推荐): 下载 `Shimeji-ee_x.x.x_Portable.zip`，解压后直接运行
- **MSI安装包**: 下载 `Shimeji-ee-x.x.x.msi`，双击安装
- **JAR版本**: 需要Java 21+环境

详细安装说明请参考 [安装指南](docs/user/INSTALL.md)

### 开发构建

```bash
# 克隆项目
git clone https://github.com/BegoniaHe/dc-ShimejiLive.git
cd dc-ShimejiLive

# 编译运行
mvn -P run

# 创建安装包
mvn -P jpackage
```

## 📚 文档

- [用户文档](docs/user/) - 安装和使用指南
- [开发文档](docs/development/) - 开发者指南
- [API文档](docs/api/) - 代码文档

## 🔧 自定义

您可以通过修改以下文件来自定义您的Shimeji：

- `conf/actions.xml` - 动作定义
- `conf/behaviors.xml` - 行为模式
- `conf/settings.properties` - 基本设置
- `img/` - 角色图像资源

详细的自定义教程请参考 [用户教程](docs/user/tutorial/)

## 🤝 贡献

欢迎贡献代码、报告问题或提出建议！

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用多重许可证：

- 原始Shimeji代码：[zlib License](https://www.zlib.net/zlib_license.html)
- Shimeji-ee扩展：[BSD-2-Clause License](https://opensource.org/license/bsd-2-clause)

## 🙏 致谢

- 原始Shimeji项目由 Yuki Yamada (Group Finity) 创建
- Shimeji-ee项目扩展功能
- 社区贡献者们的努力

## 📞 支持

- [Issues](https://github.com/BegoniaHe/dc-ShimejiLive/issues) - 报告问题
- [Discussions](https://github.com/BegoniaHe/dc-ShimejiLive/discussions) - 讨论交流