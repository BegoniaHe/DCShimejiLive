# 个性化定制 (进阶)

欢迎来到个性化定制教程！在本章中，您将学习如何从零开始，一步步创建属于您自己的 Shimeji 角色。

## 概念：什么是图片集 (Image Set)？

一个 Shimeji 角色由一个“图片集”定义。它不仅仅是一组图片，而是一个包含了角色外观、动画和行为逻辑的完整文件夹。

### 文件夹结构

一个典型的图片集文件夹结构如下：

```
img/
└── MyShimeji/
    ├── conf/
    │   ├── actions.xml
    │   └── behaviors.xml
    ├── shime1.png
    ├── shime2.png
    └── ... (其他图片)
```

1.  **`img/` 文件夹**: 这是 Shimeji-Live 存放所有图片集的根目录。
2.  **`MyShimeji/`**: 这是您角色的文件夹。文件夹的名称将成为角色的名称，并在程序中显示。
3.  **`conf/`**: 此文件夹包含角色的核心配置文件。
4.  **`actions.xml`**: 定义角色的所有“动作”，即具体的动画序列。
5.  **`behaviors.xml`**: 定义角色的“行为”，即在何种条件下执行何种动作的逻辑。
6.  **图片文件**: 所有 `.png` 图片文件都放在角色文件夹的根目录下。

## 步骤 1: 规划与绘画

在动手之前，先规划好您的角色。

-   **姿势列表**: 列出您需要的所有姿势。一个基本的角色至少需要：站立、行走、坐下、爬墙、爬天花板、下落、被拖动等。
-   **图片规格**:
    -   所有图片建议使用相同的画布尺寸，即使角色本身很小。这能让锚点计算更容易。
    -   图片必须是带有透明通道的 PNG 格式。
    -   为了动画流畅，相邻帧的图片不宜变化过大。

## 步骤 2: 编写 `actions.xml` - 定义动画

这是最核心的一步。您需要为您绘制的每一张图片都赋予生命。

1.  **从一个简单的动作开始**: 先从“站立”(`Stand`)动作开始。它通常只有一帧。

    ```xml
    <Action Name="Stand" Type="Stay" BorderType="Floor">
        <Animation>
            <Pose Image="/Stand.png" ImageAnchor="64,128" Duration="250" />
        </Animation>
    </Action>
    ```

2.  **微调锚点 (`ImageAnchor`)**: `ImageAnchor` 定义了角色的“逻辑”位置，通常是角色的脚底中心。加载您的角色并反复调试这个值，直到角色能够平稳地站立在地板上。**这是确保动画质量最关键的一步。**

3.  **创建多帧动画**: 对于“行走”(`Walk`)等动作，您需要定义多个 `<Pose>`。

    ```xml
    <Action Name="Walk" Type="Move" BorderType="Floor">
        <Animation>
            <Pose Image="/Walk1.png" ImageAnchor="64,128" Velocity="-2,0" Duration="6" />
            <Pose Image="/Walk2.png" ImageAnchor="64,128" Velocity="-2,0" Duration="6" />
        </Animation>
    </Action>
    ```
    -   确保每一帧的 `ImageAnchor` 都经过仔细调整，使得角色在动画播放时不会上下抖动。
    -   `Velocity` 定义了角色在这一帧的移动。对于向左走的动画，X 速度应为负数。

*请参考 [动作基础](./actions-foundation) 教程来了解所有动作类型和参数的详细信息。*

## 步骤 3: 编写 `behaviors.xml` - 赋予个性

`behaviors.xml` 决定了角色的“性格”。

-   一个好动的角色可能会有很高的 `Walk` 和 `Run` 行为的 `Frequency`。
-   一个懒惰的角色可能会有很高的 `Sit` 和 `LieDown` 行为的 `Frequency`。
-   通过使用带有 `Condition` 的行为，您可以让角色对环境做出反应。例如，当鼠标靠近时，执行一个特殊的“好奇”动作。

*请参考 [行为模式](./behavior-patterns) 教程来了解如何设计复杂的行为逻辑。*

## 步骤 4: 激活与测试

将您创建的 `MyShimeji` 文件夹放入 Shimeji-Live 的 `img` 目录下。然后启动程序。在角色选择菜单中，您应该能看到您的新角色。

测试所有动作和行为，确保它们符合您的预期。这个过程通常需要反复修改 `actions.xml` 和 `behaviors.xml`。

创建自己的 Shimeji 角色是一个富有创造性的过程。祝您好运！