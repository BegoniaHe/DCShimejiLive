# 动作基础 (进阶)

欢迎来到动作系统基础教程！在本章中，我们将更深入地学习如何定义 Shimeji-Live 角色的动作，并探索一些高级属性。

## 什么是动作 (Action)？

在 Shimeji-Live 中，“动作”是角色的一个基本行为单元，例如“站立”、“行走”或“爬墙”。每个动作都与一段或多段动画相关联，并且可以具有特定的行为逻辑。

动作定义在每个角色图片集目录下的 `conf/actions.xml` 文件中。

## `actions.xml` 文件结构

`actions.xml` 的根节点是 `<Mascot>`，它包含一个 `<ActionList>`。`<ActionList>` 中可以定义多个 `<Action>`。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Mascot xmlns="http://www.group-finity.com/Mascot"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.group-finity.com/Mascot Mascot.xsd">

	<ActionList>
		<!-- 在这里定义动作 -->
	</ActionList>

</Mascot>
```

## `<Action>` 标签详解

一个典型的 `<Action>` 标签如下所示：

```xml
<Action Name="Stand" Type="Stay" BorderType="Floor">
    <Animation>
        <Pose Image="/Stand.png" ImageAnchor="64,128" Velocity="0,0" Duration="250" />
    </Animation
</Action>
```

### 核心属性

-   **`Name`**: 动作的唯一名称，用于在 `behaviors.xml` 中引用。
-   **`Type`**: 动作的类型，它决定了该动作的底层 Java 类和基本行为逻辑。常见的类型有：
    -   `Stay`: 角色保持静止的动作，如“站立”或“坐下”。
    -   `Move`: 角色沿特定边界（如地板或墙壁）移动的动作。
    -   `Animate`: 一段一次性的动画，播放完毕后即结束。
    -   `Embedded`: 嵌入式的动作，其逻辑完全由一个自定义的 Java 类控制。这用于实现复杂的行为，如“跳跃”(`Jump`)或“下落”(`Fall`)。
    -   `Sequence`: 一系列按顺序执行的子动作。
    -   `Select`: 从一系列子动作中选择一个符合条件的来执行。
-   **`Class`** (仅用于 `Type="Embedded"`)：指定实现此动作逻辑的 Java 类的完整路径。
-   **`BorderType`** (用于 `Stay` 和 `Move`): 指定角色所依附的边界类型。
    -   `Floor`: 地板或窗口顶部。
    -   `Wall`: 墙壁或窗口侧边。
    -   `Ceiling`: 天花板或窗口底部。
-   **`Draggable`**: 一个布尔值，决定了在该动作期间角色是否可以被鼠标拖动。默认为 `true`。

### `<Animation>` 和 `<Pose>`

每个 `<Action>` 内部包含一个或多个 `<Animation>` 标签，代表一段动画。程序会选择第一个 `Condition` 为真的动画来播放。

每个 `<Animation>` 包含一个或多个 `<Pose>` 标签，代表动画中的一帧。

-   **`Image`**: 该帧使用的图片路径（相对于 `img` 目录）。
-   **`ImageAnchor`**: 图片的锚点，即角色的逻辑位置（脚的位置）。这是一个非常重要的属性，它决定了角色在屏幕上的精确位置。通常，您需要微调这个值以确保动画看起来平滑。
-   **`Velocity`**: 角色在该帧的移动速度 (vx, vy)，单位是像素/滴答。
-   **`Duration`**: 该帧的持续时间（以滴答数为单位，通常1滴答约10-40毫秒）。
-   **`Sound`** 和 **`Volume`**: 可以在特定帧播放声音。`Sound` 是声音文件的路径，`Volume` 是 0.0 到 1.0 之间的浮点数。

## 动作类型详解

### `Sequence` (序列)

`Sequence` 允许您将多个简单的动作串联起来，形成一个复杂的动作序列。

```xml
<Action Name="WalkLeftAlongFloorAndSit" Type="Sequence" Loop="false">
    <ActionReference Name="Walk" TargetX="200" />
    <ActionReference Name="Stand" Duration="50" />
    <ActionReference Name="Look" LookRight="true" />
    <ActionReference Name="Stand" Duration="50" />
    <ActionReference Name="Sit" Duration="1000" />
</Action>
```

-   **`Loop`**: 如果为 `true`，序列在结束后会重新开始。
-   **`ActionReference`**: 用于引用另一个已定义的动作。您可以在这里覆盖被引用动作的参数，例如为 `Stand` 动作设置一个新的 `Duration`。

在这个例子中，角色会：
1.  行走 (`Walk`) 到 X 坐标 200 的位置。
2.  站立 (`Stand`) 50 个滴答。
3.  转身 (`Look`) 向右。
4.  再站立 50 个滴答。
5.  最后坐下 (`Sit`) 1000 个滴答。

### `Select` (选择)

`Select` 会从其包含的子动作中，选择第一个满足 `Condition` 的动作来执行。这对于创建对环境有反应的行为非常有用。

```xml
<Action Name="Fall" Type="Sequence">
    <ActionReference Name="Falling" />
    <Action Type="Select">
        <Action Type="Sequence" Condition="${mascot.environment.floor.isOn(mascot.anchor)}">
            <ActionReference Name="Bouncing" />
            <ActionReference Name="Stand" />
        </Action>
        <ActionReference Name="GrabWall" />
    </Action>
</Action>
```

在这个“下落”的例子中：
1.  角色首先执行 `Falling` 动作。
2.  下落结束后，`Select` 开始工作。
3.  它检查第一个条件：角色是否在地板上 (`mascot.environment.floor.isOn(mascot.anchor)`)。
4.  如果是，则执行一个序列：先“弹跳”(`Bouncing`)，然后“站立”(`Stand`)。
5.  如果不在地板上（意味着它可能撞到了墙上），则执行第二个动作 `GrabWall`（因为它没有条件，所以总是满足）。

在下一章节中，我们将更详细地探讨如何将这些动作组合成复杂的行为模式。