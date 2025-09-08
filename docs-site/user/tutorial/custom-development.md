# 自定义开发 (进阶)

欢迎来到自定义开发教程！本章是为希望通过编写 Java 代码来扩展 Shimeji-Live 功能的开发者准备的。如果您想实现现有 XML 配置无法满足的独特功能，那么这里就是您的起点。

## 为什么要自定义开发？

虽然 `actions.xml` 和 `behaviors.xml` 提供了强大的配置能力，但有些逻辑本质上是程序性的，例如：
-   与外部应用程序或 API 交互。
-   实现复杂的物理模拟（例如，自定义的重力、弹跳效果）。
-   创建需要精确计时或复杂状态管理的动作。
-   添加全新的角色能力。

## 扩展动作系统

创建自定义动作是扩展 Shimeji-Live 功能最常见的方式。

### 创建自定义 Action 类

1.  **选择一个基类**: 您的自定义类应该继承自一个合适的基类。
    -   `com.group_finity.mascot.action.ActionBase`: 所有动作的根基类。
    -   `com.group_finity.mascot.action.Animate`: 如果您的动作是播放一段一次性动画，继承这个类会很方便。
    -   `com.group_finity.mascot.action.BorderedAction`: 如果您的动作需要依附于某个边界（地板、墙壁、天花板），这个基类处理了大部分边界检测逻辑。
    -   `com.group_finity.mascot.action.Move`: 如果您的动作涉及移动到目标点，继承此类可以简化很多工作。

2.  **实现核心方法**:
    -   `init(Mascot mascot)`: 在动作开始时调用一次。在这里初始化您的动作所需的所有变量。
    -   `hasNext()`: 每一帧开始时调用，用于判断动作是否应该继续。如果返回 `false`，动作将立即结束。
    -   `tick()`: 如果 `hasNext()` 返回 `true`，则此方法会被调用。这是您实现动作逻辑的核心。您应该在这里：
        -   更新角色的状态（例如，位置、朝向）。
        -   调用 `getAnimation().next(getMascot(), getTime())` 来推进动画帧。
        -   检查是否满足动作结束的条件。

3.  **与 XML 和脚本交互**:
    -   **获取参数**: 在 `actions.xml` 中为您的动作定义的任何自定义参数，都可以通过 `eval()` 方法在 Java 代码中获取。

        ```java
        // in actions.xml: <Action ... MyParam="123">
        
        // in your Action class:
        private int getMyParam() throws VariableException {
            return eval(getSchema().getString("MyParam"), Number.class, 0).intValue();
        }
        ```

    -   **暴露变量给脚本**: 您可以使用 `putVariable()` 方法将 Java 中的变量暴露给脚本，这样就可以在 `Condition` 表达式或 `<Pose>` 的动态属性中使用它们。

        ```java
        // in your tick() method:
        double currentSpeed = calculateSpeed();
        putVariable("CurrentSpeed", currentSpeed); 
        // now you can use #{CurrentSpeed} in actions.xml
        ```

### 在 `actions.xml` 中使用

```xml
<Action Name="MyAwesomeAction" Type="Embedded" Class="com.your.package.MyAwesomeAction" MyParam="AValue" />
```
-   `Type` 必须是 `Embedded`。
-   `Class` 是您的自定义类的完整、合格的名称。

## 编译和部署

1.  **环境设置**: 您需要一个 Java 开发环境 (JDK 8+) 和一个 IDE (如 Eclipse, IntelliJ IDEA, 或 VS Code with Java extensions)。
2.  **导入项目**: 将 Shimeji-Live 的源代码作为项目导入到您的 IDE 中。
3.  **添加代码**: 将您编写的 `.java` 文件添加到项目的源代码目录中，注意包名要匹配。
4.  **构建**: 使用 IDE 的构建功能或项目的构建脚本（如 Ant 或 Maven，如果项目有的话）来编译整个项目。这将生成一个新的 `Shimeji-ee.jar` 文件。
5.  **替换**: 用您新生成的 `.jar` 文件替换掉您 Shimeji-Live 安装目录中的同名文件。

自定义开发打开了通往无限可能性的大门。祝您编码愉快！