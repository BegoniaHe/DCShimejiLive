# 行为模式 (进阶)

欢迎来到行为模式教程！在本章中，您将更深入地学习如何使用 `behaviors.xml` 文件来控制角色的行为逻辑，塑造其独特的“个性”。

## 什么是行为 (Behavior)？

“行为”是一系列规则，它决定了角色在特定条件下会执行哪个“动作”。例如，一个行为可以定义为“当角色在地板上时，有50%的几率行走，有50%的几率坐下”。行为是连接“条件”和“动作”的桥梁。

行为定义在每个角色图片集目录下的 `conf/behaviors.xml` 文件中。

## `<Behavior>` 标签

`<Behavior>` 标签定义了一个具体的行为。它通过引用在 `actions.xml` 中定义的动作来工作。

```xml
<Behavior Name="WalkAlongWorkAreaFloor" Action="WalkAlongWorkAreaFloor" Frequency="100" />
```

-   **`Name`**: 行为的名称。它通常与对应的 `Action` 名称相同，但这并非强制要求。
-   **`Action`**: 当这个行为被触发时，要执行的动作的名称（引用自 `actions.xml`）。
-   **`Frequency`**: 此行为被选中的频率或权重。在一个条件下，所有可用行为的 `Frequency` 会被加总，每个行为被选中的概率就是其自身的频率除以总频率。
-   **`Hidden`**: 如果为 `true`，此行为不会出现在右键菜单中。这对于那些由系统或其他行为触发的中间行为非常有用。

## 条件执行: `<Condition>`

`<Condition>` 标签是塑造角色个性的核心。它允许您根据角色的当前状态和环境来决定哪些行为是可用的。

```xml
<!-- 当角色在IE窗口顶部时 -->
<Condition Condition="#{mascot.environment.activeIE.topBorder.isOn(mascot.anchor)}">
    <Behavior Name="WalkAlongIECeiling" Frequency="100" />
    <Behavior Name="JumpFromLeftEdgeOfIE" Frequency="50" />
    <Behavior Name="JumpFromRightEdgeOfIE" Frequency="50" />
</Condition>
```

-   **`Condition`**: 一个返回 `true` 或 `false` 的脚本表达式。只有当表达式为 `true` 时，内部定义的行为才会被考虑。
-   您可以堆叠多个 `<Condition>` 标签。一个行为可以出现在多个不同的条件下。

### 常用条件变量

-   `mascot.anchor`: 角色的锚点坐标 (x, y)。
-   `mascot.lookRight`: 角色是否朝右 (`true`/`false`)。
-   `mascot.totalCount`: 当前屏幕上的角色总数。
-   `mascot.environment.workArea`: 工作区（屏幕）的边界。
-   `mascot.environment.activeIE`: 当前活动窗口的边界。
-   `mascot.environment.cursor`: 鼠标光标的坐标 (x, y) 和移动速度 (dx, dy)。
-   `Math.random()`: 返回一个 0 到 1 之间的随机数，可用于创建概率性条件。

## 链接行为: `<NextBehavior>`

`<NextBehavior>` 使得行为可以一个接一个地发生，形成一个逻辑链。这对于创建“故事性”的行为序列至关重要。

```xml
<Behavior Name="LieDown" Frequency="0">
    <NextBehavior Add="false">
        <BehaviorReference Name="SitDown" Frequency="100" />
        <BehaviorReference Name="CrawlAlongWorkAreaFloor" Frequency="100" />
    </NextBehavior>
</Behavior>
```

-   **`<NextBehavior>`**: 定义了当当前行为（这里是 `LieDown`）结束后，接下来可能的行为列表。
-   **`Add`**:
    -   `false` (默认): 用这个列表 **替换** 当前所有可用的行为。这意味着，在 `LieDown` 之后，角色 **只能** 选择 `SitDown` 或 `CrawlAlongWorkAreaFloor`。
    -   `true`: 将这个列表 **添加** 到当前所有可用的行为中。这给了角色更多的选择。
-   **`<BehaviorReference>`**: 引用另一个行为，并可以为其指定新的 `Frequency`。

## 必需的核心行为

有几个行为是每个角色都必须定义的，它们对应于系统级的事件，并且通常没有随机触发的 `Frequency`。

-   **`Fall`**: 当角色失去立足点时（例如，从墙上掉下来）。这个行为通常会调用一个包含下落和着陆动画的复杂 `Sequence` 动作。
-   **`Dragged`**: 当用户用鼠标拖动角色时。
-   **`Thrown`**: 当用户拖动后松开鼠标时。
-   **`ChaseMouse`**: 一个特殊的行为，当鼠标指针在角色附近快速移动时被触发。

```xml
<Behavior Name="ChaseMouse" Frequency="0" Hidden="true" Action="ChaseMouse">
    <NextBehavior Add="false">
        <BehaviorReference Name="SitAndFaceMouse" Frequency="1" />
    </NextBehavior>
</Behavior>
```
在这个例子中，当 `ChaseMouse` 行为结束后，角色会立即切换到 `SitAndFaceMouse` 行为。

通过精心设计条件、频率和行为链，您可以创造出一个看起来有自己思想和个性的鲜活角色。