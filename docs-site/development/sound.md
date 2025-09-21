# 声音系统

Shimeji-ee 能够加载和播放声音效果，为用户的操作提供音频反馈。声音系统由 `SoundLoader` 和 `Sounds` 两个核心类组成。

## `Sounds` (声音)

`Sounds` 是一个静态类，它持有一个所有已加载声音剪辑 (`Clip`) 的并发哈希图 (`ConcurrentHashMap`)。

- **`Sounds.load(String filename, Clip clip)`**: 将一个声音剪辑加载到集合中。键是文件名和音量的组合，以防止重复加载相同文件和音量的声音。
- **`Sounds.getSound(String filename)`**: 根据文件名和音量获取一个已加载的声音剪辑。
- **`Sounds.getSoundsIgnoringVolume(String filename)`**: 获取所有匹配给定文件名（忽略音量）的声音剪辑。这在需要停止特定声音的所有实例时很有用。
- **`Sounds.isMuted()`**: 检查声音当前是否被静音。
- **`Sounds.setMuted(boolean mutedFlag)`**: 设置静音状态。如果设置为 `true`，所有当前正在播放的声音都将被停止。

## `SoundLoader` (声音加载器)

`SoundLoader` 负责从文件加载新的声音剪辑。

- **`SoundLoader.load(String name, float volume)`**: 从给定的文件路径加载声音。
    - 它首先会检查同样文件和音量的声音是否已经被加载，如果是，则直接返回。
    - 它使用 Java Sound API (`javax.sound.sampled`) 来读取音频文件并创建一个 `Clip` 对象。
    - 音量是通过 `FloatControl.Type.MASTER_GAIN` 来设置的。
    - 为 `Clip` 添加一个行监听器 (`LineListener`)，以确保在声音播放停止时，剪辑被正确地停止和关闭。
    - 加载完成后，`Clip` 对象会被存储在 `Sounds` 类的集合中。

### 例子

在 `actions.xml` 中播放一个声音：

```xml
<Action Name="Fall" Type="Stay">
    <Animation>
        <Pose Image="/img/fall.png" Duration="150" Sound="fall.wav" Volume="0.8" />
    </Animation>
</Action>
```

- 当这个动作被执行时，程序会调用 `SoundLoader.load("fall.wav", 0.8f)`。
- `SoundLoader` 会加载 `fall.wav` 文件，设置其音量为 80%，然后将其存储在 `Sounds` 集合中。
- 随后，相应的 `Clip` 会被播放。