# 配置系统

Shimeji-Live 的配置系统允许您自定义角色的行为、外观和其他设置。

## 配置文件结构

### 主要配置文件

- `conf/actions.xml` - 动作定义
- `conf/behaviors.xml` - 行为模式
- `conf/settings.properties` - 基本设置
- `conf/language.properties` - 语言配置

### 图像资源

- `img/` - 角色图像文件夹
- 支持 PNG 格式的透明图像

## 基本设置

### settings.properties

```properties
# 动画间隔（毫秒）
AnimationDuration=40

# 同时显示的角色数量
MaxMascots=5

# 是否启用音效
SoundEffects=true

# DPI 设置
MenuDPI=96
```

### 常用设置

- `AnimationDuration` - 控制动画流畅度
- `MaxMascots` - 限制性能消耗
- `SoundEffects` - 启用/禁用音效
- `MenuDPI` - 界面缩放设置

## 行为配置

### behaviors.xml 结构

```xml
<BehaviorList>
    <Behavior Name="Normal" Frequency="10">
        <NextBehaviorList>
            <BehaviorReference Behavior="Walk" Frequency="5"/>
            <BehaviorReference Behavior="Sit" Frequency="3"/>
        </NextBehaviorList>
    </Behavior>
</BehaviorList>
```

### 行为属性

- `Name` - 行为名称
- `Frequency` - 执行频率
- `Hidden` - 是否在菜单中隐藏

## 动作配置

### actions.xml 基础

```xml
<ActionList>
    <Action Name="Stand" Type="Stay">
        <Animation Condition="true" IsTurn="false">
            <Pose Image="stand.png" ImageRight="" ImageAnchor="12,34" Velocity="0,0" Duration="1000" Sound="" Volume="0"/>
            <Hotspot Shape="Rectangle" Origin="10,10" Size="20,20" Behaviour="SitDown" />
        </Animation>
    </Action>
</ActionList>
```

### `<Animation>` 属性

| 属性 | 类型 | 默认值 | 描述 |
| --- | --- | --- | --- |
| `Condition` | 脚本 | `true` | 动画生效的条件表达式。 |
| `IsTurn` | 布尔值 | `false` | 动画播放时是否需要根据吉祥物朝向（左/右）翻转图像。 |

### `<Pose>` 属性

`<Pose>` 标签定义了动画中的单帧。

| 属性 | 类型 | 描述 |
| --- | --- | --- |
| `Image` | 字符串 | 左向图像的文件路径。 |
| `ImageRight` | 字符串 | 右向图像的文件路径（可选）。如果未提供，将使用 `Image` 的水平翻转版本。 |
| `ImageAnchor` | 坐标 (x,y) | 图像的锚点，决定了图像在窗口中的定位点。 |
| `Velocity` | 坐标 (x,y) | 在此帧期间，吉祥物的水平和垂直位移。 |
| `Duration` | 整数 | 此帧的持续时间（以动画间隔为单位，通常为 40 毫秒）。 |
| `Sound` | 字符串 | 在此帧播放的音效文件路径。 |
| `Volume` | 浮点数 | 音效的音量。 |

### `<Hotspot>` 属性

`<Hotspot>` 允许在吉祥物身上定义可交互区域。当用户点击这些区域时，可以触发特定的行为。

| 属性 | 类型 | 描述 |
| --- | --- | --- |
| `Shape` | 枚举 | 热点区域的形状。支持 `Rectangle` (矩形) 和 `Ellipse` (椭圆)。 |
| `Origin` | 坐标 (x,y) | 形状的左上角坐标。 |
| `Size` | 尺寸 (宽,高) | 形状的宽度和高度。 |
| `Behaviour` | 字符串 | 点击该热点时要切换到的行为名称。 |

## 语言配置

### 多语言支持

创建对应的语言文件：

- `language_en.properties` - 英文
- `language_zh.properties` - 中文
- `language.properties` - 默认语言

### 配置示例

```properties
CallShimeji=召唤Shimeji
DismissAll=关闭程序
Settings=设置
```

## 高级配置

### 条件表达式

使用 JavaScript 语法的条件表达式：

```xml
Condition="#{mascot.anchor.x} < #{screen.width/2}"
```

### 环境变量
脚本引擎提供了多个环境变量，可用于条件表达式中以创建动态行为。这些变量使用 `#{variableName}` 语法进行访问。
- **屏幕相关变量**:
    - `#{screen.width}`: 虚拟屏幕的总宽度，可能跨越多个显示器。
    - `#{screen.height}`: 虚拟屏幕的总高度。
    - `#{screen.left}`, `#{screen.top}`, `#{screen.right}`, `#{screen.bottom}`: 主屏幕的边界。
    - `#{workArea.width}`, `#{workArea.height}`: 可用桌面区域的尺寸，不包括任务栏。
    - `#{workArea.left}`, `#{workArea.top}`, `#{workArea.right}`, `#{workArea.bottom}`: 工作区的边界。
- **吉祥物相关变量**:
    - `#{mascot.anchor.x}`: 角色当前的 X 坐标。
    - `#{mascot.anchor.y}`: 角色当前的 Y 坐标。
    - `#{mascot.lookRight}`: 一个布尔值 (`true` 或 `false`)，指示角色是否朝右。
- **示例**: 创建一个仅当吉祥物位于工作区左半部分时才为 `true` 的条件，您可以使用：
  ```xml
  Condition="#{mascot.anchor.x} < #{workArea.left} + #{workArea.width} / 2"
  ```

## 调试配置

### 启用调试模式

在 `settings.properties` 中添加：

```properties
DebugMode=true
LogLevel=DEBUG
```

### 日志配置

修改 `logging.properties`：

```properties
# 设置日志级别
.level=INFO

# 文件输出
java.util.logging.FileHandler.pattern=shimeji.log
java.util.logging.FileHandler.formatter=com.group_finity.mascot.LogFormatter
```

## 配置验证

### XML 验证

所有 XML 文件都有对应的 XSD 架构文件进行验证：

- `conf/Mascot.xsd` - 主架构文件

### 常见错误

1. XML 格式错误
2. 图像文件路径错误
3. 条件表达式语法错误
4. 属性值超出范围

## 备份与恢复

### 配置备份

定期备份配置文件：

```bash
cp -r conf/ conf_backup/
```

### 恢复默认配置

删除修改的配置文件，重启应用会自动恢复默认配置。

更多配置选项请参考源代码中的示例和文档。
