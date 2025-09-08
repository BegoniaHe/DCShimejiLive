# 逐帧动画解析 (进阶)

欢迎来到逐帧动画解析教程！在本章中，我们将深入探讨 Shimeji-Live 动画系统的一些高级特性，帮助您创造更生动、更具动态的动画效果。

## 条件动画

在 `actions.xml` 中，一个 `<Action>` 可以包含多个 `<Animation>` 标签。这允许同一个动作根据不同的条件播放不同的动画。每个 `<Animation>` 都可以有一个 `Condition` 属性。程序会按顺序检查每个动画的条件，并播放 **第一个** 满足条件的动画。

```xml
<Action Name="SitAndLookAtMouse" Type="Stay" BorderType="Floor">
    <Animation Condition="#{mascot.environment.cursor.y < mascot.environment.screen.height/2}">
        <Pose Image="/SitLookUp.png" Duration="250" />
    </Animation>
    <Animation>
        <Pose Image="/Sit.png" Duration="250" />
    </Animation>
</Action>
```

在这个例子中：
1.  **条件检查**: 每一帧，程序都会检查第一个 `<Animation>` 的条件：`#{mascot.environment.cursor.y < mascot.environment.screen.height/2}`。`#{...}` 语法意味着这个表达式每帧都会被重新计算。
2.  **播放动画**:
    -   如果鼠标光标在屏幕的上半部分，条件为 `true`，角色会播放“向上看”的动画。
    -   如果鼠标在下半部分，第一个动画的条件为 `false`，程序会继续检查下一个 `<Animation>`。第二个动画没有 `Condition` 属性，因此它总是被视为 `true`。所以，角色会播放默认的“坐下”动画。

## 动态属性与脚本

`<Pose>` 标签的几乎所有属性（如 `X`, `Y`, `ImageAnchor`, `Duration`）都可以使用脚本表达式来动态计算。这为创造复杂的动画效果提供了巨大的灵活性。

### `$` 与 `#` 的区别

-   **`${...}`**: 表达式只在 **动作开始时** 计算一次。它的值在整个动作执行期间是固定的。适用于设置一个固定的目标，如 `TargetX="${mascot.environment.workArea.right - 100}"`（走到距离右边框100像素的位置）。
-   **`#{...}`**: 表达式在 **每一帧** 都会被重新计算。适用于需要持续响应环境变化的情况，如 `TargetX="#{mascot.environment.cursor.x}"`（持续追逐鼠标）。

### 示例: 动态抛物线

`Fall` 动作的 Java 类 `com.group_finity.mascot.action.Fall` 在每一帧都会计算角色的速度和位置，并将它们存入变量中。我们可以利用这一点。

```xml
<Action Name="Falling" Type="Embedded" Class="com.group_finity.mascot.action.Fall">
    <Animation>
        <!-- 根据垂直速度选择不同的图片 -->
        <Animation Condition="#{VelocityY > 10}">
             <Pose Image="/FallFast.png" />
        </Animation>
        <Animation Condition="#{VelocityY > 0}">
             <Pose Image="/FallSlow.png" />
        </Animation>
        <Animation>
             <Pose Image="/JumpApex.png" />
        </Animation>
    </Animation>
</Action>
```
在这个虚构的例子中，角色在下落时会根据其垂直速度 `VelocityY` 显示不同的姿势。

## 特殊动作详解

### `Breed` (繁殖)

`Breed` 动作允许一个角色创造出另一个角色。

```xml
<Action Name="Divide" Type="Embedded" Class="com.group_finity.mascot.action.Breed"
        BornMascot="KuroShimeji"
        BornBehavior="Divided"
        BornX="-16" BornY="0">
    <Animation>
        <!-- 分裂动画 -->
    </Animation>
</Action>
```
-   **`BornMascot`**: 新角色的图片集名称。
-   **`BornBehavior`**: 新角色出生后立即执行的行为。
-   **`BornX`, `BornY`**: 新角色相对于父角色的出生位置偏移。

### `Transform` (变形)

`Transform` 动作可以改变角色自身。

```xml
<Action Name="Evolve" Type="Embedded" Class="com.group_finity.mascot.action.Transform"
        TransformMascot="SuperShimeji"
        TransformBehaviour="EvolvedStand">
    <Animation>
        <!-- 进化动画 -->
    </Animation>
</Action>
```
-   **`TransformMascot`**: 角色将变成的目标图片集名称。
-   **`TransformBehaviour`**: 角色变形完成后立即执行的行为。

这些高级特性为您创造富有创意和动态的角色动画提供了强大的工具。