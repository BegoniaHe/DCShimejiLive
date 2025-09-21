# 架构概览

Shimeji-Live 采用模块化的架构设计，旨在实现高度的可扩展性和可维护性。理解其核心组件和交互方式是进行二次开发或贡献代码的关键。

## 核心组件

![Architecture Diagram](https://your-image-hosting.com/architecture.png)
*(这是一个占位符，建议后续替换为真实的架构图)*

### 1. `Main` - 应用程序入口
- **`com.group_finity.mascot.Main`**
- 负责初始化应用程序、加载配置、创建主窗口和启动吉祥物管理器。

### 2. `Manager` - 吉祥物管理器
- **`com.group_finity.mascot.Manager`**
- 管理所有吉祥物实例的生命周期。
- 维护一个吉祥物列表，并以固定的时间间隔（tick）更新每个吉祥物的状态。
- 处理吉祥物的创建、销毁和交互事件。

### 3. `Mascot` - 吉祥物实例
- **`com.group_finity.mascot.Mascot`**
- 代表屏幕上的一个独立角色。
- 包含了角色的状态信息，如当前位置 (`anchor`)、图像 (`image`)、行为 (`behavior`) 等。
- 每个 `Mascot` 实例都在自己的窗口 (`window`) 中绘制。

### 4. `Behavior` - 行为系统
- **`com.group_finity.mascot.behavior`**
- 驱动吉祥物“思考”的核心。`Behavior` 定义了吉祥物的状态机。
- `Behavior` 接口 (`com.group_finity.mascot.behavior.Behavior`) 定义了所有行为必须实现的通用方法：
    - `init(Mascot mascot)`: 初始化行为。
    - `next()`: 在每个时钟周期（tick）调用，用于更新吉祥物状态或决定是否切换到下一个行为。
    - `mousePressed(MouseEvent e)` / `mouseReleased(MouseEvent e)`: 处理鼠标交互事件。
- `UserBehavior` 是一个关键实现，它处理了大部分用户交互，如拖拽 (`Dragged`)、抛出 (`Thrown`) 和落地 (`Fall`) 之间的状态转换。
- `Behavior` 本身不执行具体动作，而是决定 **下一个** 应该执行什么 `Action`。
- 行为逻辑在 `behaviors.xml` 中定义，通过 `BehaviorBuilder` 进行解析和加载。
- 在每个 `tick`，`Manager` 会调用当前 `Mascot` 的 `behavior.next()` 方法来更新其状态。

### 5. `Action` - 动作系统
- **`com.group_finity.mascot.action`**
- `Action` 是行为的具体执行者。它负责改变吉祥物的状态，例如移动位置、切换动画帧等。
- 动作在 `actions.xml` 中定义，通过 `ActionBuilder` 解析。
- `Action` 分为多种类型，如 `Move` (移动), `Stay` (静止), 和 `Sequence` (动作序列)。

### 6. `Animation` - 动画系统
- **`com.group_finity.mascot.animation`**
- `Animation` 由一个或多个 `Pose` (姿势) 组成，并且可以包含一个 `Condition` (条件)。只有当条件满足时，该动画才会被执行。
- 每个 `Pose` (`com.group_finity.mascot.animation.Pose`) 代表动画中的一帧。它定义了：
    - `image`/`rightImage`: 当前帧使用的图像。
    - `duration`: 该帧的持续时间（以 tick 为单位）。
    - `dx`/`dy`: 在该帧内，吉祥物在 X 和 Y 轴上的位移。
    - `sound`: 在该帧播放的音效。
- `Animation` 负责根据时间流逝切换 `Mascot` 当前显示的图像，从而形成动画效果。动画的加载逻辑由 `AnimationBuilder` (`com.group_finity.mascot.config.AnimationBuilder`) 处理。

### 7. `Configuration` - 配置加载
- **`com.group_finity.mascot.config`**
- 负责解析 XML 配置文件 (`actions.xml`, `behaviors.xml`)。
- 使用 `Configuration` 类作为入口，加载并验证这些文件，然后通过 `ActionBuilder` 和 `BehaviorBuilder` 创建相应的对象实例。

### 8. `Environment` - 环境感知
- **`com.group_finity.mascot.environment`**
- 该包提供了对桌面环境的详细抽象，允许吉祥物与屏幕边界、窗口和其他桌面元素进行交互。这对于创建逼真的交互至关重要，例如在地板上行走、爬墙或挂在天花板上。
- **关键类**:
    - `Environment`: 一个抽象类，定义了环境检测的核心逻辑。它管理屏幕信息 (`screen`)、复杂屏幕区域 (`complexScreen`) 和光标位置 (`cursor`)。特定平台的实现扩展了此类以提供具体功能。
    - `Area`: 表示屏幕上的一个矩形区域，例如显示器或窗口。它包括 `left`、`top`、`right` 和 `bottom` 边界的属性。`ComplexArea` 类管理这些 `Area` 对象的集合，以处理多显示器设置。
    - `Border`: 屏幕边界的接口。主要实现包括：
        - `FloorCeiling`: 表示水平边界（地板和天花板）。它检查吉祥物是否在地板或天花板上，并在区域移动时计算其新位置。
        - `Wall`: 表示垂直边界（墙壁）。它为垂直表面执行类似的检查和计算。
    - `MascotEnvironment`: 提供以吉祥物为中心的环境视图。它确定吉祥物当前的工作区域，识别活动窗口（例如 IE 浏览器），并找到最近的地板、天花板或墙壁进行交互。该类确保吉祥物的行为是情境感知的。
- 吉祥物的行为会根据 `Environment` 提供的信息做出反应，例如在屏幕边缘行走或爬墙。这是实现平台相关功能的关键，通过 `NativeFactory` 为不同操作系统 (Windows, macOS, Linux) 提供具体实现。

## 数据流与交互

1.  **启动**: `Main` 启动，加载 `settings.properties`。
2.  **加载配置**: `Manager` 初始化，`Configuration` 读取并解析所有 mascot 的 `actions.xml` 和 `behaviors.xml` 文件。
3.  **创建吉祥物**: `Manager` 创建一个或多个 `Mascot` 实例。每个 `Mascot` 被赋予一个初始的 `Behavior` (通常是 `Fall`)。
4.  **主循环 (Tick)**:
    - `Manager` 的定时器触发 `tick()` 方法。
    - 遍历所有 `Mascot` 实例。
    - 调用 `mascot.getBehavior().next()`。
    - `Behavior` 逻辑判断是否需要切换到下一个 `Action`。
    - 如果切换，`Mascot` 的 `action` 属性被更新。
    - 调用当前 `mascot.getAction().apply()`。
    - `Action` 根据其定义（例如，移动），更新 `Mascot` 的 `anchor` (位置)。
    - `Action` 同时更新 `Mascot` 的 `animation` 状态。
    - `Mascot` 的 `tick()` 方法被调用，它会更新 `animation` 的当前帧。
    - `Mascot` 的 `window` 根据新的位置和图像进行重绘。
5.  **交互**:
    - 用户拖动窗口时，`Mascot` 的 `behavior` 会被强制切换到 `Dragged`。
    - 拖动结束后，通常会切换回 `Fall` 行为。

## 扩展点

- **添加新动作/行为**: 无需修改 Java 代码，只需编辑 `actions.xml` 和 `behaviors.xml`。
- **添加自定义 `Action`**:
  1.  创建一个继承自 `com.group_finity.mascot.action.Action` 的 Java 类。
  2.  在 `actions.xml` 中使用 `Class` 属性引用您的新类。
- **支持新平台**:
  1.  实现 `com.group_finity.mascot.environment.NativeEnvironment` 和 `com.group_finity.mascot.image.NativeImage` 接口。
  2.  创建一个新的 `com.group_finity.mascot.NativeFactory` 实现来提供您的平台特定类。

理解这个架构将帮助您更有效地为 Shimeji-Live 添加新功能或修复问题。