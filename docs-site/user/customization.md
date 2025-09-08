# 自定义角色

Shimeji-Live 强大的功能之一就是支持高度自定义的角色。您可以创造属于自己的、独一无二的桌面伙伴！本指南将带您一步步了解如何创建和配置自定义角色。

## 角色文件结构

每个角色都包含在一个独立的文件夹中，通常位于 `img/` 目录下。一个典型的角色文件夹结构如下：

```
img/
└── MyMascot/
    ├── conf/
    │   ├── actions.xml
    │   └── behaviors.xml
    ├── shime1.png
    ├── shime2.png
    └── ...
```

- **`img/MyMascot/`**: 角色根目录，`MyMascot` 是您角色的名字。
- **`conf/`**: 存放配置文件的目录。
  - **`actions.xml`**: 定义角色的所有 **动作**。
  - **`behaviors.xml`**: 定义角色的所有 **行为**。
- **`.png` 文件**: 角色的图像资源，每一帧都是一个独立的 PNG 文件。

## 创建第一步：图像资源

1. **绘制图像**:
   - 绘制您角色的不同姿势，例如站立、行走、爬墙、坐下等。
   - 每个姿势的每一帧都应保存为独立的、透明背景的 PNG 文件。
   - **建议尺寸**: 图像尺寸不宜过大，通常在 128x128 像素范围内效果最佳。

2. **命名规范**:
   - 给您的图像文件起一个有意义的名字，例如 `stand.png`, `walk1.png`, `walk2.png`。这将有助于您在后续配置中引用它们。

## 第二步：定义动作 (`actions.xml`)

`actions.xml` 文件定义了角色的 **具体动作**，例如“向左走”或“站立”。每个动作由一个或多个“姿势” (Pose) 组成，每个姿势对应一个图像文件和持续时间。

### 示例: 一个简单的站立动作

```xml
<ActionList>
  <Action Name="Stand" Type="Stay">
    <Animation>
      <Pose Image="stand.png" ImageAnchor="Bottom" Duration="1000"/>
      <Pose Image="stand_blink.png" ImageAnchor="Bottom" Duration="200"/>
    </Animation>
  </Action>
</ActionList>
```

- **`Action`**: 定义一个动作。
  - **`Name`**: 动作的唯一名称，例如 `Stand`。
  - **`Type`**: 动作类型，`Stay` 表示静止。
- **`Animation`**: 定义一个动画序列。
- **`Pose`**: 定义动画的 **一帧**。
  - **`Image`**: 引用的图像文件名。
  - **`ImageAnchor`**: 图像的锚点，`Bottom` 表示图像底部与地面接触。
  - **`Duration`**: 该帧的持续时间（毫秒）。

### 示例: 行走动画

```xml
<Action Name="WalkLeft" Type="Move">
  <Animation Loop="true">
    <Pose Image="walk_left1.png" ImageAnchor="Bottom" Velocity="-2,0" Duration="500"/>
    <Pose Image="walk_left2.png" ImageAnchor="Bottom" Velocity="-2,0" Duration="500"/>
  </Animation>
</Action>
```

- **`Type="Move"`**: 表示这是一个移动动作。
- **`Loop="true"`**: 表示动画将循环播放。
- **`Velocity="-2,0"`**: 定义了移动速度，`-2` 表示每帧向左移动 2 像素，`0` 表示垂直方向不变。

## 第三步：定义行为 (`behaviors.xml`)

`behaviors.xml` 文件将多个动作串联起来，形成 **行为逻辑**。它决定了角色在什么情况下会做什么动作，以及动作之间的转换关系。

### 示例: 定义一个“站立”和“行走”的行为

```xml
<BehaviorList>
  <Behavior Name="Fall" InitialAction="Fall">
    <NextBehaviorList>
      <BehaviorReference Behavior="Land" />
    </NextBehaviorList>
  </Behavior>

  <Behavior Name="Land" InitialAction="Land">
      <NextBehaviorList>
          <BehaviorReference Behavior="Stand" />
      </NextBehaviorList>
  </Behavior>

  <Behavior Name="Stand" InitialAction="Stand" Frequency="10">
    <NextBehaviorList>
      <BehaviorReference Behavior="Walk" Frequency="5"/>
      <BehaviorReference Behavior="Sit" Frequency="3"/>
    </NextBehaviorList>
  </Behavior>

  <Behavior Name="Walk" InitialAction="WalkLeft">
      <NextBehaviorList>
          <BehaviorReference Behavior="Stand" Frequency="1" />
      </NextBehaviorList>
  </Behavior>
</BehaviorList>
```

- **`Behavior`**: 定义一个行为。
  - **`Name`**: 行为的唯一名称。
  - **`InitialAction`**: 该行为开始时执行的第一个动作（在 `actions.xml` 中定义）。
  - **`Frequency`**: 该行为被选择的频率权重。数值越高，越容易被触发。
- **`NextBehaviorList`**: 定义当前行为结束后 **可能转换** 的下一个行为列表。
- **`BehaviorReference`**: 引用另一个行为。

在这个例子中：
1.  角色会从 `Fall`（下落）行为开始。
2.  结束后转换到 `Land`（着陆）行为。
3.  着陆后转换到 `Stand`（站立）行为。
4.  在站立时，它有一定概率转换到 `Walk` (行走) 或 `Sit` (坐下) 行为。
5.  行走结束后，它会转换回 `Stand` 行为。

## 第四步：应用您的角色

1. 将您创建的角色文件夹（例如 `MyMascot`）放入 `img/` 目录下。
2. 启动 Shimeji-Live。
3. 在右键菜单的“选择角色”中，您应该能看到并选择您的新角色！

## 高级技巧

- **条件动作**: 您可以在 `actions.xml` 中使用 `Condition` 属性，让动作只在特定条件下触发。例如，只在屏幕左侧时才向右走。
- **复制和修改**: 最快的入门方法是复制一个现有角色（例如 `img/Shimeji/`），然后在此基础上修改图像和配置文件。

恭喜！您现在已经掌握了创建自定义 Shimeji 角色的基础知识。发挥您的创意，创造更多有趣的桌面伙伴吧！