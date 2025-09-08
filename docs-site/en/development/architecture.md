# Architecture Overview

Shimeji-Live uses a modular architectural design to achieve high scalability and maintainability. Understanding its core components and their interactions is key to secondary development or contributing code.

## Core Components

![Architecture Diagram](https://your-image-hosting.com/architecture.png)
*(This is a placeholder; it is recommended to replace it with a real architecture diagram later)*

### 1. `Main` - Application Entry Point
- **`com.group_finity.mascot.Main`**
- Responsible for initializing the application, loading configurations, creating the main window, and starting the mascot manager.

### 2. `Manager` - Mascot Manager
- **`com.group_finity.mascot.Manager`**
- Manages the lifecycle of all mascot instances.
- Maintains a list of mascots and updates the state of each mascot at a fixed time interval (tick).
- Handles the creation, destruction, and interaction events of mascots.

### 3. `Mascot` - Mascot Instance
- **`com.group_finity.mascot.Mascot`**
- Represents a single character on the screen.
- Contains the state information of the character, such as its current position (`anchor`), image (`image`), behavior (`behavior`), etc.
- Each `Mascot` instance is drawn in its own window (`window`).

### 4. `Behavior` - Behavior System
- **`com.group_finity.mascot.behavior`**
- The core that drives the mascot's "thinking." `Behavior` defines the mascot's state machine.
- The `Behavior` interface (`com.group_finity.mascot.behavior.Behavior`) defines common methods that all behaviors must implement:
    - `init(Mascot mascot)`: Initializes the behavior.
    - `next()`: Called at each clock tick to update the mascot's state or decide whether to switch to the next behavior.
    - `mousePressed(MouseEvent e)` / `mouseReleased(MouseEvent e)`: Handles mouse interaction events.
- `UserBehavior` is a key implementation that handles most user interactions, such as state transitions between being dragged (`Dragged`), thrown (`Thrown`), and falling (`Fall`).
- `Behavior` itself does not perform specific actions but decides what `Action` should be executed **next**.
- Behavior logic is defined in `behaviors.xml` and is parsed and loaded by the `BehaviorBuilder`.
- At each `tick`, the `Manager` calls the current `Mascot`'s `behavior.next()` method to update its state.

### 5. `Action` - Action System
- **`com.group_finity.mascot.action`**
- `Action` is the concrete executor of a behavior. It is responsible for changing the mascot's state, such as moving its position or switching animation frames.
- Actions are defined in `actions.xml` and parsed by the `ActionBuilder`.
- `Action` is divided into various types, such as `Move`, `Stay`, and `Sequence`.

### 6. `Animation` - Animation System
- **`com.group_finity.mascot.animation`**
- An `Animation` consists of one or more `Pose`s and can include a `Condition`. The animation will only be executed if the condition is met.
- Each `Pose` (`com.group_finity.mascot.animation.Pose`) represents a single frame in the animation. It defines:
    - `image`/`rightImage`: The image used for the current frame.
    - `duration`: The duration of this frame in ticks.
    - `dx`/`dy`: The displacement of the mascot on the X and Y axes within this frame.
    - `sound`: The sound effect to play during this frame.
- `Animation` is responsible for switching the `Mascot`'s currently displayed image over time, thus creating an animation effect. The animation loading logic is handled by the `AnimationBuilder` (`com.group_finity.mascot.config.AnimationBuilder`).

### 7. `Configuration` - Configuration Loading
- **`com.group_finity.mascot.config`**
- Responsible for parsing XML configuration files (`actions.xml`, `behaviors.xml`).
- Uses the `Configuration` class as an entry point to load and validate these files, then creates the corresponding object instances through `ActionBuilder` and `BehaviorBuilder`.

### 8. `Environment` - Environment Awareness
- **`com.group_finity.mascot.environment`**
- This package provides a detailed abstraction of the desktop environment, allowing mascots to interact with screen boundaries, windows, and other desktop elements. This is crucial for creating realistic interactions, such as walking on the floor, climbing walls, or hanging from the ceiling.
- **Key Classes**:
    - `Environment`: An abstract class that defines the core logic for environment detection. It manages screen information (`screen`), complex screen areas (`complexScreen`), and cursor location (`cursor`). Platform-specific implementations extend this class to provide concrete functionality.
    - `Area`: Represents a rectangular region on the screen, such as a monitor or a window. It includes properties for `left`, `top`, `right`, and `bottom` boundaries. The `ComplexArea` class manages a collection of these `Area` objects to handle multi-monitor setups.
    - `Border`: An interface for screen boundaries. Key implementations include:
        - `FloorCeiling`: Represents horizontal boundaries (floors and ceilings). It checks if a mascot is on a floor or ceiling and calculates its new position if the area moves.
        - `Wall`: Represents vertical boundaries (walls). It performs similar checks and calculations for vertical surfaces.
    - `MascotEnvironment`: Provides a mascot-centric view of the environment. It determines the mascot's current work area, identifies the active window (e.g., an IE browser), and finds the nearest floor, ceiling, or wall for interaction. This class ensures that the mascot's behavior is context-aware.
- The mascot's behavior will react based on the information provided by the `Environment`, such as walking along the edge of the screen or climbing a wall. This is key to implementing platform-specific functionality, providing concrete implementations for different operating systems (Windows, macOS, Linux) through `NativeFactory`.

## Data Flow and Interaction

1.  **Startup**: `Main` starts and loads `settings.properties`.
2.  **Load Configuration**: `Manager` initializes, and `Configuration` reads and parses all mascots' `actions.xml` and `behaviors.xml` files.
3.  **Create Mascot**: `Manager` creates one or more `Mascot` instances. Each `Mascot` is assigned an initial `Behavior` (usually `Fall`).
4.  **Main Loop (Tick)**:
    -   The `Manager`'s timer triggers the `tick()` method.
    -   It iterates through all `Mascot` instances.
    -   It calls `mascot.getBehavior().next()`.
    -   The `Behavior` logic determines if it needs to switch to the next `Action`.
    -   If a switch occurs, the `Mascot`'s `action` property is updated.
    -   It calls the current `mascot.getAction().apply()`.
    -   The `Action`, according to its definition (e.g., move), updates the `Mascot`'s `anchor` (position).
    -   The `Action` also updates the `Mascot`'s `animation` state.
    -   The `Mascot`'s `tick()` method is called, which updates the current frame of the `animation`.
    -   The `Mascot`'s `window` is redrawn based on the new position and image.
5.  **Interaction**:
    -   When the user drags the window, the `Mascot`'s `behavior` is forcibly switched to `Dragged`.
    -   After dragging ends, it usually switches back to the `Fall` behavior.

## Extension Points

-   **Add New Actions/Behaviors**: No need to modify Java code; just edit `actions.xml` and `behaviors.xml`.
-   **Add Custom `Action`**:
    1.  Create a Java class that inherits from `com.group_finity.mascot.action.Action`.
    2.  Use the `Class` attribute in `actions.xml` to reference your new class.
-   **Support New Platforms**:
    1.  Implement the `com.group_finity.mascot.environment.NativeEnvironment` and `com.group_finity.mascot.image.NativeImage` interfaces.
    2.  Create a new `com.group_finity.mascot.NativeFactory` implementation to provide your platform-specific classes.

Understanding this architecture will help you add new features or fix issues in Shimeji-Live more effectively.