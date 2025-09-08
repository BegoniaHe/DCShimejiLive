# 高级互动 (进阶)

欢迎来到高级互动教程！在本章中，我们将深入探讨 Shimeji-Live 的互动系统，并解析其工作原理。

## 互动系统概述

Shimeji-Live 的互动系统基于一个“广播”和“扫描”的机制，允许角色之间进行动态交互。

1.  **广播 (Broadcast)**: 一个角色（广播者）会发出一个“信号”，表明它愿意进行某种互动。这个信号被称为 **Affordance** (示能)，是一个简单的字符串标签，例如 "Hug"。
2.  **扫描 (Scan)**: 另一个角色（扫描者）会主动寻找持有特定 Affordance 的角色。
3.  **执行 (Execute)**: 一旦扫描者找到了目标，它会移动到目标旁边。当它们位置充分接近时，扫描者和目标都会切换到预设的互动行为，并开始播放相应的动画。

## 关键动作类

实现这一机制的核心 Java 动作类是：

-   `com.group_finity.mascot.action.Broadcast`: 附加一个 Affordance 到角色身上，使其可以被其他角色扫描到。
-   `com.group_finity.mascot.action.ScanMove`: 扫描带有特定 Affordance 的角色，并驱动角色向目标移动。
-   `com.group_finity.mascot.action.ScanJump`: 与 `ScanMove` 类似，但通过跳跃的方式接近目标。
-   `com.group_finity.mascot.action.Interact`: 在两个角色位置重叠时执行的互动动画。这个动作会持续进行，直到两个角色不再重叠。

## 示例: 抱抱互动 (详细解析)

让我们以 `actions.xml` 和 `behaviors.xml` 中的“抱抱”互动为例来完整地分析这个过程。

### 1. 广播抱抱需求 (`BroadcastHug`)

一个角色通过执行 `BroadcastHug` 动作来表达“想要抱抱”的意愿。

**actions.xml:**
```xml
<Action Name="BroadcastHug" Type="Embedded" Class="com.group_finity.mascot.action.Broadcast" Affordance="Hug">
    <Animation>
        <Pose Image="/Sit.png" Duration="250" />
    </Animation>
</Action>
```
-   `Class="...Broadcast"`: 使用广播动作类。
-   **`Affordance="Hug"`**: 这是关键。当此动作执行时，角色会获得一个名为 "Hug" 的标签。只要该角色在执行此动作，这个标签就一直存在。

**behaviors.xml:**
```xml
<Behavior Name="BroadcastHugBehavior" Action="BroadcastHug" Frequency="120" />
```
-   这个行为使得角色有一定几率随机地开始广播抱抱需求。

### 2. 寻找并移动到抱抱对象 (`FindAndHug`)

另一个角色执行 `FindAndHug` 动作来寻找可以抱抱的对象。

**actions.xml:**
```xml
<Action Name="FindAndHug" Type="Embedded" Class="com.group_finity.mascot.action.ScanMove" Affordance="Hug" Behavior="IHugYou" TargetBehavior="IAmHugged">
    <Animation>
        <!-- 行走动画帧 -->
    </Animation>
</Action>
```
-   `Class="...ScanMove"`: 使用扫描移动动作类。
-   **`Affordance="Hug"`**: 它会在屏幕上寻找任何一个带有 "Hug" 标签的角色。
-   **`Behavior="IHugYou"`**: 这是为扫描者自己准备的。当扫描者成功到达目标旁边时，它会自动将自己的行为切换到 "IHugYou"。
-   **`TargetBehavior="IAmHugged"`**: 这是为被找到的目标角色准备的。当扫描者到达时，目标角色的行为会被强制切换为 "IAmHugged"。

### 3. 执行互动动画

`IHugYou` 和 `IAmHugged` 行为会分别触发主动抱和被抱的动作。

**actions.xml:**
```xml
<Action Name="HugAction" Type="Embedded" Class="com.group_finity.mascot.action.Interact">
    <Animation>
        <!-- 主动抱抱的动画帧 -->
    </Animation>
</Action>

<Action Name="HuggedAction" Type="Embedded" Class="com.group_finity.mascot.action.Interact">
    <Animation>
        <!-- 被动被抱的动画帧 -->
    </Animation>
</Action>
```
-   `Class="...Interact"`: 使用互动动作类。这个类的特殊之处在于，它会一直执行，直到两个互动的角色不再接触。一旦分开，动作就会结束。

### 4. 行为的配合

最后，`behaviors.xml` 将所有部分串联起来，形成完整的逻辑链：

**behaviors.xml:**
```xml
<!-- 行为1: 随机广播抱抱需求 -->
<Behavior Name="BroadcastHugBehavior" Action="BroadcastHug" Frequency="120" />

<!-- 行为2: 随机寻找抱抱对象 -->
<Behavior Name="FindHugBehavior" Action="FindAndHug" Frequency="100" />

<!-- 行为3 & 4: 互动行为，由ScanMove触发，所以频率为0 -->
<Behavior Name="IHugYou" Action="HugAction" Frequency="0" Hidden="true" />
<Behavior Name="IAmHugged" Action="HuggedAction" Frequency="0" Hidden="true" />
```

通过这个系统，您可以设计出各种各样的互动。关键在于想好：
1.  互动的“信号”是什么 (Affordance)。
2.  谁是发起者，谁是接受者。
3.  到达后双方各自应该做什么 (Behavior 和 TargetBehavior)。