# Custom Characters

One of the most powerful features of Shimeji-Live is its support for highly customizable characters. You can create your own unique desktop companions! This guide will walk you through the steps to create and configure custom characters.

## Character File Structure

Each character is contained in a separate folder, usually located in the `img/` directory. A typical character folder structure looks like this:

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

- **`img/MyMascot/`**: The root directory for the character, where `MyMascot` is your character's name.
- **`conf/`**: The directory for configuration files.
  - **`actions.xml`**: Defines all the character's **actions**.
  - **`behaviors.xml`**: Defines all the character's **behaviors**.
- **`.png` files**: The character's image assets, with each frame being a separate PNG file.

## Step 1: Image Assets

1.  **Draw Images**:
    -   Draw your character in various poses, such as standing, walking, climbing walls, sitting, etc.
    -   Each frame of each pose should be saved as a separate PNG file with a transparent background.
    -   **Recommended Size**: Images should not be too large; a size within 128x128 pixels usually works best.

2.  **Naming Convention**:
    -   Give your image files meaningful names, like `stand.png`, `walk1.png`, `walk2.png`. This will help you reference them in the configuration later.

## Step 2: Define Actions (`actions.xml`)

The `actions.xml` file defines the character's **specific actions**, such as "WalkLeft" or "Stand". Each action consists of one or more "Poses", where each pose corresponds to an image file and a duration.

### Example: A Simple Stand Action

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

-   **`Action`**: Defines an action.
    -   **`Name`**: The unique name of the action, e.g., `Stand`.
    -   **`Type`**: The action type, `Stay` means stationary.
-   **`Animation`**: Defines an animation sequence.
-   **`Pose`**: Defines **one frame** of the animation.
    -   **`Image`**: The referenced image filename.
    -   **`ImageAnchor`**: The anchor point of the image. `Bottom` means the bottom of the image touches the ground.
    -   **`Duration`**: The duration of this frame in milliseconds.

### Example: Walking Animation

```xml
<Action Name="WalkLeft" Type="Move">
  <Animation Loop="true">
    <Pose Image="walk_left1.png" ImageAnchor="Bottom" Velocity="-2,0" Duration="500"/>
    <Pose Image="walk_left2.png" ImageAnchor="Bottom" Velocity="-2,0" Duration="500"/>
  </Animation>
</Action>
```

-   **`Type="Move"`**: Indicates this is a movement action.
-   **`Loop="true"`**: Indicates the animation will play in a loop.
-   **`Velocity="-2,0"`**: Defines the movement speed. `-2` means moving 2 pixels to the left per frame, and `0` means no vertical change.

## Step 3: Define Behaviors (`behaviors.xml`)

The `behaviors.xml` file links multiple actions together to form **behavior logic**. It determines what actions the character will perform under what circumstances and the transition relationships between actions.

### Example: Defining "Stand" and "Walk" Behaviors

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

-   **`Behavior`**: Defines a behavior.
    -   **`Name`**: The unique name of the behavior.
    -   **`InitialAction`**: The first action to execute when this behavior starts (defined in `actions.xml`).
    -   **`Frequency`**: The weight for selecting this behavior. A higher value means it's more likely to be triggered.
-   **`NextBehaviorList`**: Defines the list of possible next behaviors after the current one ends.
-   **`BehaviorReference`**: References another behavior.

In this example:
1.  The character starts with the `Fall` behavior.
2.  It then transitions to the `Land` behavior.
3.  After landing, it transitions to the `Stand` behavior.
4.  While standing, it has a chance to transition to `Walk` or `Sit`.
5.  After walking, it transitions back to `Stand`.

## Step 4: Apply Your Character

1.  Place your created character folder (e.g., `MyMascot`) into the `img/` directory.
2.  Launch Shimeji-Live.
3.  In the right-click menu under "Choose Mascot," you should see and be able to select your new character!

## Advanced Tips

-   **Conditional Actions**: You can use the `Condition` attribute in `actions.xml` to make actions trigger only under specific conditions, e.g., only walk right if on the left side of the screen.
-   **Copy and Modify**: The quickest way to get started is to copy an existing character (like `img/Shimeji/`) and modify its images and configuration files.

Congratulations! You now know the basics of creating a custom Shimeji character. Use your creativity to bring more fun companions to your desktop!