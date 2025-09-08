# Configuration System

The configuration system of Shimeji-Live allows you to customize the behavior, appearance, and other settings of your characters.

## Configuration File Structure

### Main Configuration Files

- `conf/actions.xml` - Action definitions
- `conf/behaviors.xml` - Behavior patterns
- `conf/settings.properties` - Basic settings
- `conf/language.properties` - Language configuration

### Image Assets

- `img/` - Character image folder
- Supports transparent PNG format images

## Basic Settings

### settings.properties

```properties
# Animation interval (milliseconds)
AnimationDuration=40

# Maximum number of concurrently displayed characters
MaxMascots=5

# Whether to enable sound effects
SoundEffects=true

# DPI settings
MenuDPI=96
```

### Common Settings

- `AnimationDuration` - Controls animation smoothness
- `MaxMascots` - Limits performance consumption
- `SoundEffects` - Enables/disables sound effects
- `MenuDPI` - UI scaling settings

## Behavior Configuration

### behaviors.xml Structure

```xml
<BehaviorList>
    <Behavior Name="Normal" Frequency="10">
        <NextBehaviorList>
            <BehaviorReference Behavior="Walk" Frequency="5"/>
            <BehaviorReference Behavior="Sit" Frequency="3"/>
        </NextBehaviorList>
    </Behavior>
</BehaviorList>
```

### Behavior Attributes

- `Name` - Behavior name
- `Frequency` - Execution frequency
- `Hidden` - Whether to hide in the menu

## Action Configuration

### actions.xml Basics

```xml
<ActionList>
    <Action Name="Stand" Type="Stay">
        <Animation Condition="true" IsTurn="false">
            <Pose Image="stand.png" ImageRight="" ImageAnchor="12,34" Velocity="0,0" Duration="1000" Sound="" Volume="0"/>
            <Hotspot Shape="Rectangle" Origin="10,10" Size="20,20" Behaviour="SitDown" />
        </Animation>
    </Action>
</ActionList>
```

### `<Animation>` Attributes

| Attribute | Type | Default | Description |
| --- | --- | --- | --- |
| `Condition` | Script | `true` | The conditional expression for the animation to take effect. |
| `IsTurn` | Boolean | `false` | Whether to flip the image based on the mascot's direction (left/right) when the animation plays. |

### `<Pose>` Attributes

The `<Pose>` tag defines a single frame in an animation.

| Attribute | Type | Description |
| --- | --- | --- |
| `Image` | String | File path for the left-facing image. |
| `ImageRight` | String | File path for the right-facing image (optional). If not provided, a horizontally flipped version of `Image` will be used. |
| `ImageAnchor` | Coordinates (x,y) | The anchor point of the image, which determines its positioning point in the window. |
| `Velocity` | Coordinates (x,y) | The horizontal and vertical displacement of the mascot during this frame. |
| `Duration` | Integer | The duration of this frame (in units of animation interval, usually 40 ms). |
| `Sound` | String | The file path of the sound effect to play during this frame. |
| `Volume` | Float | The volume of the sound effect. |

### `<Hotspot>` Attributes

The `<Hotspot>` allows you to define interactive areas on the mascot. When the user clicks these areas, specific behaviors can be triggered.

| Attribute | Type | Description |
| --- | --- | --- |
| `Shape` | Enum | The shape of the hotspot area. Supports `Rectangle` and `Ellipse`. |
| `Origin` | Coordinates (x,y) | The top-left corner coordinates of the shape. |
| `Size` | Dimensions (width,height) | The width and height of the shape. |
| `Behaviour` | String | The name of the behavior to switch to when this hotspot is clicked. |

## Language Configuration

### Multi-language Support

Create corresponding language files:

- `language_en.properties` - English
- `language_zh.properties` - Chinese
- `language.properties` - Default language

### Configuration Example

```properties
CallShimeji=Call Shimeji
DismissAll=Dismiss All
Settings=Settings
```

## Advanced Configuration

### Conditional Expressions

Use conditional expressions with JavaScript syntax:

```xml
Condition="#{mascot.anchor.x} < #{screen.width/2}"
```

### Environment Variables
The scripting engine provides access to several environment variables that can be used in conditional expressions to create dynamic behaviors. These variables are accessed using the `#{variableName}` syntax.
- **Screen-related variables**:
    - `#{screen.width}`: The total width of the virtual screen, which may span multiple monitors.
    - `#{screen.height}`: The total height of the virtual screen.
    - `#{screen.left}`, `#{screen.top}`, `#{screen.right}`, `#{screen.bottom}`: The boundaries of the primary screen.
    - `#{workArea.width}`, `#{workArea.height}`: The dimensions of the usable desktop area, excluding the taskbar.
    - `#{workArea.left}`, `#{workArea.top}`, `#{workArea.right}`, `#{workArea.bottom}`: The boundaries of the work area.
- **Mascot-related variables**:
    - `#{mascot.anchor.x}`: The character's current X coordinate.
    - `#{mascot.anchor.y}`: The character's current Y coordinate.
    - `#{mascot.lookRight}`: A boolean (`true` or `false`) indicating if the character is facing right.
- **Example**: To create a condition that's true only when the mascot is on the left half of the work area, you would use:
  ```xml
  Condition="#{mascot.anchor.x} < #{workArea.left} + #{workArea.width} / 2"
  ```

## Debugging Configuration

### Enable Debug Mode

Add in `settings.properties`:

```properties
DebugMode=true
LogLevel=DEBUG
```

### Log Configuration

Modify `logging.properties`:

```properties
# Set log level
.level=INFO

# File output
java.util.logging.FileHandler.pattern=shimeji.log
java.util.logging.FileHandler.formatter=com.group_finity.mascot.LogFormatter
```

## Configuration Validation

### XML Validation

All XML files have corresponding XSD schema files for validation:

- `conf/Mascot.xsd` - Main schema file

### Common Errors

1. XML format errors
2. Incorrect image file paths
3. Conditional expression syntax errors
4. Attribute values out of range

## Backup and Restore

### Configuration Backup

Periodically back up configuration files:

```bash
cp -r conf/ conf_backup/
```

### Restore Default Configuration

Delete the modified configuration files and restart the application to automatically restore the default settings.

For more configuration options, please refer to the examples and documentation in the source code.