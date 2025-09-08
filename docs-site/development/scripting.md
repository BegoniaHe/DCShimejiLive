# 脚本系统

Shimeji-ee 的行为由脚本驱动，这些脚本允许动态计算数值和复杂的逻辑。脚本系统主要由以下几个核心类组成。

## `Variable` (变量)

`Variable` 是一个抽象基类，代表一个可以被求值的实体。它有两个主要的具体实现：`Constant` 和 `Script`。

- **`Variable.parse(String source)`**: 这是一个静态工厂方法，用于从一个字符串源解析出一个 `Variable` 对象。
    - 如果源字符串以 `${` 和 `}` 包裹，它会被解析为一个 `Script` 对象，该脚本在每次行为帧初始化时不会被清除。
    - 如果源字符串以 `#{` 和 `}` 包裹，它会被解析为一个 `Script` 对象，该脚本的值会在每次行为帧初始化时被清除。
    - 否则，该字符串会被解析为一个 `Constant`。

## `Constant` (常量)

`Constant` 类代表一个不变的值。它可以是 `null`、布尔值 (`true`/`false`)、数字 (浮点数) 或字符串。

## `Script` (脚本)

`Script` 类代表一个 JavaScript 表达式，它可以在运行时被求值。脚本引擎使用了 Mozilla Rhino。

- **安全性**: 为了防止恶意脚本，脚本执行环境受到严格限制。
    - 优化级别被禁用，以解释模式运行。
    - 设置了指令计数器阈值，以防止无限循环或长时间运行的脚本。
    - 禁用了动态作用域等不安全的特性。
- **求值**:
    - 脚本在第一次被请求值时进行求值。
    - 求值结果会被缓存，直到被清除。
    - `#{...}` 形式的脚本的缓存值在每一帧开始时都会被清除 (`initFrame`)，这意味着它们每帧都会重新计算。
    - `${...}` 形式的脚本的缓存值在整个行为初始化时才被清除 (`init`)，它们的值在行为执行期间保持不变，除非被显式清除。

## `VariableMap` (变量图)

`VariableMap` 是一个将字符串键映射到 `Variable` 对象的容器。它在脚本执行时充当变量的作用域。

- 当脚本被求值时，`VariableMap` 中的所有变量都会被注入到 JavaScript 的作用域中，允许脚本访问其他的变量和常量。
- 为了防止在解析变量时出现无限递归 (例如，一个脚本变量依赖于另一个脚本变量)，系统会安全地获取变量值。对于脚本变量，它会尝试使用其缓存值。

### 例子

假设 `actions.xml` 中有以下定义：

```xml
<Action Name="Sit" Type="Stay">
    <Animation>
        <Pose Image="/img/shime1.png" Duration="150" />
        <Pose Image="/img/shime2.png" Duration="150" X="${Environment.Area.Left+10}" Y="100" />
    </Animation>
</Action>
```

- `${Environment.Area.Left+10}` 会被解析为一个 `Script` 对象。
- 在动画执行时，脚本 `${Environment.Area.Left+10}` 会被求值。
- `Environment.Area.Left` 是 `VariableMap` 中预定义的一个变量。脚本会读取这个变量的值，加上 10，然后返回结果作为 `X` 坐标。