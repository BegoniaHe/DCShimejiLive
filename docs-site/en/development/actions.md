# Action System Developer Reference

This document is a technical reference for the Action System in Shimeji-Live, intended for developers creating custom behaviors.

## The `<Action>` Tag

Actions are defined in `actions.xml` using the `<Action>` tag.

```xml
<Action Name="UniqueActionName" Type="ActionType" Class="com.example.MyCustomAction">
    <!-- Parameters and Animations go here -->
</Action>
```

-   **`Name`**: A unique identifier.
-   **`Type`**: The built-in action type (e.g., `Move`).
-   **`Class`**: The fully-qualified Java class for custom actions.

## General Action Parameters

These parameters, defined as `<Variable>` tags inside `<Action>`, apply to most actions.

| Parameter | Type | Default | Description |
| --- | --- | --- | --- |
| `Condition` | Script | `true` | A JavaScript expression that must be `true` for the action to run. |
| `Duration` | Integer | `Infinite` | The maximum time in milliseconds the action can run. |
| `Draggable` | Boolean | `true` | Allows the user to drag the mascot during this action. |
| `Affordance`| String | `""` | A tag for mascot-to-mascot interaction. |

---

## Built-in Action Type Reference

### Core Actions

-   **`Animate`**: Plays an animation without moving the mascot.
-   **`Stay`**: Keeps the mascot stationary while playing an animation.
-   **`Move`**: Moves the mascot towards a target coordinate.
    -   **Parameters**: `TargetX`, `TargetY` (can be scripted values).
-   **`Jump`**: Simulates a jump to a target coordinate.
    -   **Parameters**: `TargetX`, `TargetY`, `Velocity`.
-   **`Fall`**: Makes the mascot fall until it hits the ground.
-   **`Interact`**: Plays an animation and then switches to a new behavior.
    -   **Parameter**: `Behaviour` (the name of the behavior to switch to).
-   **`Look`**: Turns the mascot to look at the mouse.
-   **`Turn`**: Flips the mascot's direction.
-   **`Sequence`**: Executes a series of other actions in order using `<ActionReference>` tags.
-   **`Offset`**: Moves the mascot by a relative X/Y offset.
    -   **Parameters**: `OffsetX`, `OffsetY`.
-   **`Transform`**: Changes the mascot's image set.
    -   **Parameters**: `ImageSet`, `Behaviour`.
-   **`Mute`**: Toggles sound.

### Specialized Actions

These are more complex, often Java-driven actions for specific scenarios:

-   **`Breed` / `BreedJump` / `BreedMove`**: Creates new mascots.
-   **`Broadcast` actions**: Sends a signal that other mascots can react to.
-   **`Dragged`**: The action used while the user is dragging the mascot.
-   **`FallWithIE` / `ThrowIE` / `WalkWithIE`**: Legacy actions for interacting with IE windows.
-   **`Scan` actions (`ScanInteract`, `ScanJump`, `ScanMove`)**: Scans for a condition (like an affordance) before executing.
-   **`SelfDestruct`**: Removes the mascot.

## Creating Custom Actions in Java

For complex logic, you can extend `com.group_finity.mascot.action.ActionBase`.

1.  Create your Java class extending `ActionBase`.
2.  Implement the `tick()` method with your logic.
3.  Use `eval(getSchema().getString("MyParameter"), ...)` to read custom parameters.
4.  Reference your class in `actions.xml` using the `Class` attribute.

```xml
<Action Name="MyAsAction" Class="com.example.MyCustomAction">
    <Variable Name="MyParameter" Value="5" />
</Action>