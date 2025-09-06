# Shimeji 动画编辑器使用指南 / Animation Editor Usage Guide

## 概述 / Overview

动画编辑器是一个可视化工具，用于编辑Shimeji桌面宠物的动画配置文件。它提供了类似VSCode的界面布局，让用户可以直观地创建和编辑动画。

The Animation Editor is a visual tool for editing Shimeji desktop pet animation configuration files. It provides a VSCode-like interface layout for intuitive animation creation and editing.

## 界面布局 / Interface Layout

编辑器界面分为三个主要区域（比例 1:2:1）：
The editor interface is divided into three main areas (ratio 1:2:1):

### 左侧面板 / Left Panel (25%)

- **动画项目树 / Animation Project Tree**
  - 显示 `./img` 目录下的所有项目文件夹
  - 每个项目包含其动画动作列表
  - Shows all project folders under `./img` directory
  - Each project contains its animation actions list

### 中间面板 / Center Panel (50%)

#### 上方：动画预览区 / Top: Animation Preview Area (60%)

- **实时动画预览 / Real-time Animation Preview**
- **播放控制 / Playback Controls:**
  - 播放 / Play：正常速度播放动画
  - 停止 / Stop：停止播放并重置到第一帧
  - 慢速播放 / Slow Play：10倍慢速播放，便于观察细节
- **进度显示 / Progress Display:**
  - 当前帧信息 / Current frame information
  - 播放进度条 / Playback progress bar

#### 下方：文件浏览器 / Bottom: File Browser (40%)

- **图片资源列表 / Image Resource List**
  - 显示当前项目的所有图片文件
  - 支持缩略图预览
  - Shows all image files in current project
  - Supports thumbnail preview

### 右侧面板 / Right Panel (25%)

- **动作详细信息编辑 / Action Details Editor**

#### 基本信息 / Basic Information

- 名称 / Name：动作名称
- 类型 / Type：Stay, Move, Animate, Sequence, Select, Embedded
- 类 / Class：Java类名（可选）
- 边界类型 / Border Type：Floor, Wall, Ceiling（可选）

#### 动画姿势列表 / Animation Poses List

- **每一帧详细信息显示 / Detailed Frame Information:**

  - 帧编号 / Frame number
  - 图片路径 / Image path
  - 锚点坐标 / Anchor coordinates
  - 速度向量 / Velocity vector
  - 持续时间 / Duration
  - 音效文件（可选）/ Sound file (optional)
- **帧操作按钮 / Frame Operations:**

  - 添加姿势 / Add Pose
  - 删除姿势 / Remove Pose
  - 上移 / Move Up
  - 下移 / Move Down

#### 姿势详情编辑 / Pose Details Editor

选中列表中的任意一帧后，在下方可以编辑：
After selecting any frame in the list, you can edit below:

- **图片 / Image**：选择图片文件
- **图片锚点 / Image Anchor**：格式为"x,y"，定义图片的锚点位置
- **速度 / Velocity**：格式为"x,y"，定义移动速度
- **持续时间 / Duration**：该帧显示的时间（毫秒）
- **音效 / Sound**：播放该帧时的音效文件（可选）
- **音量 / Volume**：音效音量（0-100%）

## 基本操作流程 / Basic Workflow

### 1. 启动编辑器 / Launch Editor

- 在Shimeji主程序的系统托盘菜单中点击"动画编辑器 / Animation Editor"
- Click "Animation Editor" in Shimeji's system tray menu

### 2. 选择项目 / Select Project

- 在左侧项目树中展开项目文件夹
- 点击任意动画动作查看其详情
- Expand project folders in the left project tree
- Click any animation action to view its details

### 3. 预览动画 / Preview Animation

- 选中动画后，中间预览区会显示动画信息
- 使用播放控制按钮预览动画效果
- After selecting animation, preview area shows animation info
- Use playback control buttons to preview animation effects

### 4. 编辑动画 / Edit Animation

#### 编辑基本信息 / Edit Basic Information

- 在右侧面板修改动画名称、类型等基本属性
- Modify animation name, type and other basic properties in right panel

#### 添加/编辑帧 / Add/Edit Frames

1. 点击"添加姿势"添加新帧 / Click "Add Pose" to add new frame
2. 在姿势列表中选择要编辑的帧 / Select frame to edit in poses list
3. 在下方详情面板中修改帧参数 / Modify frame parameters in details panel below
4. 使用"浏览"按钮选择图片文件 / Use "Browse" button to select image files

#### 调整帧顺序 / Adjust Frame Order

- 选中帧后使用"上移"/"下移"按钮调整顺序
- Use "Move Up"/"Move Down" buttons to adjust order after selecting frame

### 5. 保存项目 / Save Project

- 使用菜单栏"文件">"保存项目"保存修改
- Use menu bar "File" > "Save Project" to save changes

## 快捷操作 / Quick Operations

### 创建新动作 / Create New Action

- 菜单栏"编辑">"新建动作" / Menu bar "Edit" > "New Action"
- 输入动作名称并选择类型 / Enter action name and select type

### 删除动作 / Delete Action

- 选中动作后，菜单栏"编辑">"删除动作" / Select action, then menu bar "Edit" > "Delete Action"

### 复制现有动作 / Copy Existing Action

- 可以复制现有动作作为新动作的基础（通过创建新动作后手动复制参数）
- You can copy existing actions as basis for new actions (by manually copying parameters after creating new action)

## 注意事项 / Important Notes

1. **备份原文件** / **Backup Original Files**

   - 编辑前请备份原始的 `actions.xml` 文件
   - Please backup original `actions.xml` files before editing
2. **图片路径** / **Image Paths**

   - 图片路径相对于项目根目录，以"/"开头
   - Image paths are relative to project root, starting with "/"
3. **锚点坐标** / **Anchor Coordinates**

   - 锚点定义图片的参考点，通常是图片底部中心
   - Anchor defines reference point of image, usually bottom center
4. **速度向量** / **Velocity Vector**

   - 正值向右/向下移动，负值向左/向上移动
   - Positive values move right/down, negative values move left/up
5. **持续时间** / **Duration**

   - 以毫秒为单位，建议范围 1-1000
   - In milliseconds, recommended range 1-1000

## 故障排除 / Troubleshooting

### 动画预览不显示 / Animation Preview Not Showing

- 检查图片文件是否存在于正确的路径
- 确认图片格式为 PNG、JPG 等支持的格式
- Check if image files exist in correct path
- Confirm image format is PNG, JPG or other supported formats

### 无法保存项目 / Cannot Save Project

- 检查文件权限 / Check file permissions
- 确认项目目录可写 / Confirm project directory is writable

### 动画播放异常 / Animation Playback Issues

- 检查每帧的持续时间设置 / Check duration settings for each frame
- 确认图片锚点坐标正确 / Confirm image anchor coordinates are correct

## 技术支持 / Technical Support

如遇到问题，请查看日志文件 `ShimejieeLog0.log` 获取详细错误信息。
For issues, please check log file `ShimejieeLog0.log` for detailed error information.
