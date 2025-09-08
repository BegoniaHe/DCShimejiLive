package com.group_finity.mascot.animationeditor.ui;

import com.group_finity.mascot.animationeditor.model.Project;
import com.group_finity.mascot.animationeditor.model.AnimationAction;
import com.group_finity.mascot.animationeditor.util.ProjectScanner;
import com.group_finity.mascot.animationeditor.util.XMLConfigUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * 主编辑器窗口 - 仿VSCode界面布局，左:中:右 = 1:2:1
 * Main Editor Window - VSCode-like Layout
 * 
 * @author DCShimeji Team
 */
public class EditorMainWindow extends JFrame {
    private static final Logger log = Logger.getLogger(EditorMainWindow.class.getName());
    
    // UI Components
    private JSplitPane mainSplitPane;
    private JSplitPane rightSplitPane;
    private JSplitPane centerSplitPane;
    
    // Left Panel (1/4) - Project Tree
    private JPanel leftPanel;
    private JTree projectTree;
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    
    // Center Top Panel (2/4 top) - Animation Preview
    private JPanel centerTopPanel;
    private AnimationPreviewPanel previewPanel;
    
    // Center Bottom Panel (2/4 bottom) - Native File Browser
    private JPanel centerBottomPanel;
    private JLabel currentDirectoryLabel;
    private JButton browseButton;
    private JPanel imageGridPanel;
    private JScrollPane imageScrollPane;
    
    // Right Panel (1/4) - Action Details
    private JPanel rightPanel;
    private ActionDetailsPanel actionDetailsPanel;
    
    // Data
    private Project currentProject;
    private AnimationAction selectedAction;
    private File currentImageDirectory;
    
    public EditorMainWindow() {
        initializeUI();
        setupLayout();
        setupEventHandlers();
        // 不自动加载项目，用户需要手动点击刷新按钮加载
        // Don't auto-load projects, user needs to click refresh button
    }
    
    private void initializeUI() {
        setTitle("Shimeji 动画编辑器 / Animation Editor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        
        // Set Look and Feel for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            log.log(Level.WARNING, "Failed to set system look and feel", e);
        }
        
        // Initialize components
        createLeftPanel();
        createCenterTopPanel();
        createCenterBottomPanel();
        createRightPanel();
    }
    
    private void createLeftPanel() {
        leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(new EmptyBorder(8, 8, 8, 4));
        leftPanel.setPreferredSize(new Dimension(280, 0));
        
        // Project tree
        rootNode = new DefaultMutableTreeNode("动画项目 / Animation Projects");
        treeModel = new DefaultTreeModel(rootNode);
        projectTree = new JTree(treeModel);
        projectTree.setRootVisible(true);
        projectTree.setCellRenderer(new ProjectTreeCellRenderer());
        projectTree.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        // Add some spacing
        projectTree.setRowHeight(26);
        
        JScrollPane treeScrollPane = new JScrollPane(projectTree);
        treeScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLoweredBevelBorder(),
            "项目和动作 / Projects & Actions",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font(Font.SANS_SERIF, Font.BOLD, 14)
        ));
        
        leftPanel.add(treeScrollPane, BorderLayout.CENTER);
        
        // Add refresh button
        JButton refreshButton = new JButton("刷新项目 / Refresh");
        refreshButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        refreshButton.addActionListener(e -> loadProjects());
        leftPanel.add(refreshButton, BorderLayout.SOUTH);
    }
    
    private void createCenterTopPanel() {
        centerTopPanel = new JPanel(new BorderLayout());
        centerTopPanel.setBorder(new EmptyBorder(8, 4, 4, 4));
        centerTopPanel.setPreferredSize(new Dimension(0, 350));
        
        previewPanel = new AnimationPreviewPanel();
        centerTopPanel.add(previewPanel, BorderLayout.CENTER);
    }
    
    private void createCenterBottomPanel() {
        centerBottomPanel = new JPanel(new BorderLayout());
        centerBottomPanel.setBorder(new EmptyBorder(4, 4, 8, 4));
        
        // Top toolbar with directory info and browse button
        JPanel toolbarPanel = new JPanel(new BorderLayout());
        toolbarPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        currentDirectoryLabel = new JLabel("选择项目以浏览图片 / Select project to browse images");
        currentDirectoryLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        
        browseButton = new JButton("浏览文件夹 / Browse Folder");
        browseButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        browseButton.addActionListener(e -> browseForImageDirectory());
        browseButton.setEnabled(false);
        
        toolbarPanel.add(currentDirectoryLabel, BorderLayout.CENTER);
        toolbarPanel.add(browseButton, BorderLayout.EAST);
        
        // Image grid panel with 3 images per row (larger panels)
        imageGridPanel = new JPanel();
        imageGridPanel.setLayout(new GridLayout(0, 3, 15, 15)); // 3 columns, 15px spacing
        imageGridPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        imageGridPanel.setBackground(new Color(248, 249, 250)); // Light background
        
        imageScrollPane = new JScrollPane(imageGridPanel);
        imageScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLoweredBevelBorder(),
            "图片资源 / Image Resources",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font(Font.SANS_SERIF, Font.BOLD, 14)
        ));
        imageScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        centerBottomPanel.add(toolbarPanel, BorderLayout.NORTH);
        centerBottomPanel.add(imageScrollPane, BorderLayout.CENTER);
    }
    
    private void createRightPanel() {
        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(new EmptyBorder(8, 4, 8, 8));
        rightPanel.setPreferredSize(new Dimension(350, 0));
        
        actionDetailsPanel = new ActionDetailsPanel();
        
        JScrollPane rightScrollPane = new JScrollPane(actionDetailsPanel);
        rightScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLoweredBevelBorder(),
            "动作详情 / Action Details",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font(Font.SANS_SERIF, Font.BOLD, 14)
        ));
        
        rightPanel.add(rightScrollPane, BorderLayout.CENTER);
    }
    
    private void setupLayout() {
        // Create center panel (top and bottom)
        centerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        centerSplitPane.setTopComponent(centerTopPanel);
        centerSplitPane.setBottomComponent(centerBottomPanel);
        centerSplitPane.setDividerLocation(350);
        centerSplitPane.setResizeWeight(0.4);
        centerSplitPane.setOneTouchExpandable(true);
        
        // Create main layout (left, center, right)
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Set up the split panes: left | center | right (1:2:1 ratio)
        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(rightSplitPane);
        mainSplitPane.setDividerLocation(280);
        mainSplitPane.setResizeWeight(0.2);
        mainSplitPane.setOneTouchExpandable(true);
        
        rightSplitPane.setLeftComponent(centerSplitPane);
        rightSplitPane.setRightComponent(rightPanel);
        rightSplitPane.setDividerLocation(700);
        rightSplitPane.setResizeWeight(0.67);
        rightSplitPane.setOneTouchExpandable(true);
        
        add(mainSplitPane, BorderLayout.CENTER);
        
        // Menu bar
        createMenuBar();
        
        // Status bar
        createStatusBar();
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("文件 / File");
        
        JMenuItem openProjectItem = new JMenuItem("打开项目 / Open Project");
        openProjectItem.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        openProjectItem.addActionListener(e -> openProject());
        fileMenu.add(openProjectItem);
        
        JMenuItem saveProjectItem = new JMenuItem("保存项目 / Save Project");
        saveProjectItem.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        saveProjectItem.addActionListener(e -> saveProject());
        fileMenu.add(saveProjectItem);
        
        fileMenu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem("退出 / Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        exitItem.addActionListener(e -> dispose());
        fileMenu.add(exitItem);
        
        menuBar.add(fileMenu);
        
        // Edit menu
        JMenu editMenu = new JMenu("编辑 / Edit");
        
        JMenuItem newActionItem = new JMenuItem("新建动作 / New Action");
        newActionItem.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        newActionItem.addActionListener(e -> createNewAction());
        editMenu.add(newActionItem);
        
        JMenuItem deleteActionItem = new JMenuItem("删除动作 / Delete Action");
        deleteActionItem.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
        deleteActionItem.addActionListener(e -> deleteSelectedAction());
        editMenu.add(deleteActionItem);
        
        menuBar.add(editMenu);
        
        // View menu
        JMenu viewMenu = new JMenu("视图 / View");
        
        JMenuItem expandAllItem = new JMenuItem("展开所有 / Expand All");
        expandAllItem.addActionListener(e -> expandAllTreeNodes());
        viewMenu.add(expandAllItem);
        
        JMenuItem collapseAllItem = new JMenuItem("折叠所有 / Collapse All");
        collapseAllItem.addActionListener(e -> collapseAllTreeNodes());
        viewMenu.add(collapseAllItem);
        
        menuBar.add(viewMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        statusBar.add(new JLabel("就绪 / Ready"));
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // Project tree selection with improved feedback
        projectTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) projectTree.getLastSelectedPathComponent();
            if (selectedNode == null) {
                selectAction(null);
                return;
            }
            
            Object userObject = selectedNode.getUserObject();
            if (userObject instanceof Project) {
                Project project = (Project) userObject;
                loadProject(project);
                log.info("Loaded project: " + project.getName());
                updateStatusMessage("已加载项目: " + project.getName() + " / Loaded project: " + project.getName());
            } else if (userObject instanceof AnimationAction) {
                AnimationAction action = (AnimationAction) userObject;
                selectAction(action);
                log.info("Selected action: " + action.getName());
                updateStatusMessage("已选择动作: " + action.getName() + " / Selected action: " + action.getName());
            }
        });
        
        // Image grid panel click handling will be added to individual image panels
        // during loadImagesFromDirectory method
    }
    
    private void loadProjects() {
        try {
            File imgDirectory = new File("./img");
            if (!imgDirectory.exists()) {
                imgDirectory = new File("img");
            }
            
            List<File> projectDirs = ProjectScanner.scanProjects(imgDirectory);
            
            rootNode.removeAllChildren();
            
            for (File projectDir : projectDirs) {
                Project project = new Project(projectDir.getName(), projectDir);
                if (ProjectScanner.isValidProject(projectDir)) {
                    // Load actions from XML
                    List<AnimationAction> actions = XMLConfigUtil.loadActionsFromXML(
                        ProjectScanner.getConfigFile(projectDir));
                    project.setActions(actions);
                    
                    DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(project);
                    
                    rootNode.add(projectNode);
                }
            }
            
            treeModel.reload();
            expandAllTreeNodes();
            updateStatusMessage("已加载 " + projectDirs.size() + " 个项目 / Loaded " + projectDirs.size() + " projects");
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to load projects", e);
            JOptionPane.showMessageDialog(this, 
                "加载项目失败: " + e.getMessage() + "\nFailed to load projects: " + e.getMessage(), 
                "错误 / Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadProject(Project project) {
        this.currentProject = project;
        this.currentImageDirectory = project.getRootDirectory();
        
        // Update directory label
        currentDirectoryLabel.setText("当前目录 / Current: " + currentImageDirectory.getAbsolutePath());
        browseButton.setEnabled(true);
        
        // Load images from project directory
        loadImagesFromDirectory(currentImageDirectory);
        
        // Clear action selection
        selectAction(null);
        previewPanel.setAction(null);
    }
    
    private void loadImagesFromDirectory(File directory) {
        SwingUtilities.invokeLater(() -> {
            imageGridPanel.removeAll();
            
            if (directory != null && directory.exists() && directory.isDirectory()) {
                List<File> imageFiles = ProjectScanner.scanImageFiles(directory);
                for (File imageFile : imageFiles) {
                    addImageToGrid(imageFile);
                }
            }
            
            imageGridPanel.revalidate();
            imageGridPanel.repaint();
        });
    }
    
    private void addImageToGrid(File imageFile) {
        // Create image panel using the optimized renderer
        JPanel imagePanel = ImageGridPanelRenderer.createImagePanel(imageFile, 
            () -> {
                updateStatusMessage("选择了图片: " + imageFile.getName() + " / Selected image: " + imageFile.getName());
                // Highlight this panel among all grid panels
                for (Component comp : imageGridPanel.getComponents()) {
                    if (comp instanceof JPanel) {
                        comp.setBackground(Color.WHITE);
                        ((JPanel) comp).setBorder(BorderFactory.createRaisedBevelBorder());
                    }
                }
            }, 
            () -> {
                if (selectedAction != null) {
                    actionDetailsPanel.setImageForSelectedPose("/" + imageFile.getName());
                    updateStatusMessage("设置图片: " + imageFile.getName() + " / Set image: " + imageFile.getName());
                }
            });
        
        imageGridPanel.add(imagePanel);
    }
    
    private void selectAction(AnimationAction action) {
        this.selectedAction = action;
        actionDetailsPanel.setAction(action);
        previewPanel.setAction(action);
        
        if (action != null) {
            updateStatusMessage("编辑动作: " + action.getName() + " / Editing action: " + action.getName());
        } else {
            updateStatusMessage("未选择动作 / No action selected");
        }
    }
    
    private void browseForImageDirectory() {
        JFileChooser chooser = new JFileChooser(currentImageDirectory);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("选择图片目录 / Select Image Directory");
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedDir = chooser.getSelectedFile();
            currentImageDirectory = selectedDir;
            currentDirectoryLabel.setText("当前目录 / Current: " + currentImageDirectory.getAbsolutePath());
            loadImagesFromDirectory(selectedDir);
        }
    }
    
    // Menu action implementations
    private void openProject() {
        JFileChooser chooser = new JFileChooser("./img");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("选择项目目录 / Select Project Directory");
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedDir = chooser.getSelectedFile();
            if (ProjectScanner.isValidProject(selectedDir)) {
                loadProjects(); // Reload all projects
            } else {
                JOptionPane.showMessageDialog(this, 
                    "选中的目录不是有效的项目目录（缺少 conf/actions.xml 文件）\n" +
                    "The selected directory is not a valid project directory (missing conf/actions.xml file)",
                    "无效项目 / Invalid Project", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void saveProject() {
        if (currentProject == null) {
            JOptionPane.showMessageDialog(this, 
                "请先选择一个项目\nPlease select a project first",
                "无项目 / No Project", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean success = XMLConfigUtil.saveActionsToXML(
            currentProject.getActions(), 
            currentProject.getConfigFile());
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "项目保存成功\nProject saved successfully",
                "保存成功 / Save Success", JOptionPane.INFORMATION_MESSAGE);
            updateStatusMessage("项目已保存 / Project saved");
        } else {
            JOptionPane.showMessageDialog(this, 
                "项目保存失败\nProject save failed",
                "保存失败 / Save Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void createNewAction() {
        if (currentProject == null) {
            JOptionPane.showMessageDialog(this, 
                "请先选择一个项目\nPlease select a project first",
                "无项目 / No Project", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String actionName = JOptionPane.showInputDialog(this, 
            "输入动作名称\nEnter action name:", "新动作 / New Action", JOptionPane.QUESTION_MESSAGE);
        
        if (actionName != null && !actionName.trim().isEmpty()) {
            AnimationAction newAction = new AnimationAction(actionName.trim(), "Stay");
            currentProject.getActions().add(newAction);
            
            // Refresh tree
            loadProjects();
            updateStatusMessage("创建了新动作: " + actionName + " / Created new action: " + actionName);
        }
    }
    
    private void deleteSelectedAction() {
        if (selectedAction == null) {
            JOptionPane.showMessageDialog(this, 
                "请先选择一个动作\nPlease select an action first",
                "无选择 / No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this,
            "确定要删除动作 \"" + selectedAction.getName() + "\" 吗？\n" +
            "Are you sure you want to delete action \"" + selectedAction.getName() + "\"?",
            "确认删除 / Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            currentProject.getActions().remove(selectedAction);
            selectAction(null);
            loadProjects();
            updateStatusMessage("删除了动作 / Deleted action");
        }
    }
    
    private void expandAllTreeNodes() {
        for (int i = 0; i < projectTree.getRowCount(); i++) {
            projectTree.expandRow(i);
        }
    }
    
    private void collapseAllTreeNodes() {
        for (int i = projectTree.getRowCount() - 1; i >= 1; i--) {
            projectTree.collapseRow(i);
        }
    }
    
    private void updateStatusMessage(String message) {
        // This could update a status bar if we had one
        log.info(message);
    }
}
