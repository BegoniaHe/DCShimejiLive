package com.group_finity.mascot.animationeditor.ui;

import com.group_finity.mascot.Main;
import com.group_finity.mascot.animationeditor.model.Project;
import com.group_finity.mascot.animationeditor.model.AnimationAction;
import com.group_finity.mascot.animationeditor.util.ProjectScanner;
import com.group_finity.mascot.animationeditor.util.XMLConfigUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * 主编辑器窗口 - 仿VSCode界面布局
 * Main Editor Window - VSCode-like Layout
 * 
 * @author DCShimeji Team
 */
public class MainEditorWindow extends JFrame {
    private static final Logger log = Logger.getLogger(MainEditorWindow.class.getName());
    
    // Language bundle for i18n
    private ResourceBundle languageBundle;
    
    // UI Components
    private JSplitPane mainSplitPane;
    private JSplitPane centerSplitPane;
    
    // Left Panel Components (Animation Groups)
    private JPanel leftPanel;
    private JTree projectTree;
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    
    // Center Top Panel Components (Animation Preview)
    private JPanel centerTopPanel;
    private AnimationPreviewPanel previewPanel;
    
    // Center Bottom Panel Components (File Browser)
    private JPanel centerBottomPanel;
    private JPanel imageGridPanel;
    private JScrollPane imageScrollPane;
    
    // Right Panel Components (Action Details)
    private JPanel rightPanel;
    private ActionDetailsPanel actionDetailsPanel;
    
    // Data
    private Project currentProject;
    private AnimationAction selectedAction;
    private int currentProjectIndex = 0; // Track current project index
    private List<File> availableProjects; // Store all available projects
    private boolean projectModified = false; // Track if project has unsaved changes
    
    // Status bar
    private JLabel statusLabel;
    
    public MainEditorWindow() {
        // Initialize language bundle
        languageBundle = Main.getInstance().getLanguageBundle();
        
        initializeUI();
        setupLayout();
        setupEventHandlers();
        loadProjects();
    }
    
    private void initializeUI() {
        setTitle(languageBundle.getString("editor.title"));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        
        // Initialize components
        createLeftPanel();
        createCenterTopPanel();
        createCenterBottomPanel();
        createRightPanel();
    }
    
    private void createLeftPanel() {
        leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        leftPanel.setPreferredSize(new Dimension(250, 0));
        
        // Project tree
        rootNode = new DefaultMutableTreeNode(languageBundle.getString("editor.projects.root"));
        treeModel = new DefaultTreeModel(rootNode);
        projectTree = new JTree(treeModel);
        projectTree.setRootVisible(true);
        projectTree.setCellRenderer(new ProjectTreeCellRenderer());
        
        JScrollPane treeScrollPane = new JScrollPane(projectTree);
        treeScrollPane.setBorder(BorderFactory.createTitledBorder(languageBundle.getString("editor.projects.title")));
        
        leftPanel.add(treeScrollPane, BorderLayout.CENTER);
    }
    
    private void createCenterTopPanel() {
        centerTopPanel = new JPanel(new BorderLayout());
        centerTopPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Animation preview
        previewPanel = new AnimationPreviewPanel();
        
        JScrollPane previewScrollPane = new JScrollPane(previewPanel);
        previewScrollPane.setBorder(BorderFactory.createTitledBorder(languageBundle.getString("editor.preview.title")));
        
        centerTopPanel.add(previewScrollPane, BorderLayout.CENTER);
    }
    
    private void createCenterBottomPanel() {
        centerBottomPanel = new JPanel(new BorderLayout());
        centerBottomPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Image grid panel with 3 images per row (larger panels)
        imageGridPanel = new JPanel();
        imageGridPanel.setLayout(new GridLayout(0, 3, 15, 15)); // 3 columns, 15px spacing
        imageGridPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        imageGridPanel.setBackground(new Color(248, 249, 250)); // Light background

        imageScrollPane = new JScrollPane(imageGridPanel);
        imageScrollPane.setBorder(BorderFactory.createTitledBorder(languageBundle.getString("editor.images.title")));
        imageScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        centerBottomPanel.add(imageScrollPane, BorderLayout.CENTER);
    }    private void createRightPanel() {
        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        actionDetailsPanel = new ActionDetailsPanel();
        
        // Add change listener to track modifications
        actionDetailsPanel.addChangeListener(() -> markProjectAsModified());
        
        JScrollPane rightScrollPane = new JScrollPane(actionDetailsPanel);
        rightScrollPane.setBorder(BorderFactory.createTitledBorder(languageBundle.getString("editor.details.title")));
        
        rightPanel.add(rightScrollPane, BorderLayout.CENTER);
    }
    
    private void setupLayout() {
        // Main split pane (left:center:right = 1:2:1)
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setResizeWeight(0.25); // Left panel takes 25%
        
        // Center split pane (top and bottom)
        centerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        centerSplitPane.setTopComponent(centerTopPanel);
        centerSplitPane.setBottomComponent(centerBottomPanel);
        centerSplitPane.setDividerLocation(300);
        centerSplitPane.setResizeWeight(0.6); // Preview takes 60% of center area
        
        // Create a container for center and right panels
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        rightSplitPane.setLeftComponent(centerSplitPane);
        rightSplitPane.setRightComponent(rightPanel);
        rightSplitPane.setDividerLocation(600);
        rightSplitPane.setResizeWeight(0.67); // Center takes 2/3, right takes 1/3
        
        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(rightSplitPane);
        mainSplitPane.setDividerLocation(350);
        
        add(mainSplitPane, BorderLayout.CENTER);
        
        // Status bar
        createStatusBar();
        
        // Menu bar
        createMenuBar();
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu(languageBundle.getString("editor.menu.file"));
        
        JMenuItem openProjectItem = new JMenuItem(languageBundle.getString("editor.menu.file.open"));
        openProjectItem.addActionListener(e -> openProject());
        fileMenu.add(openProjectItem);
        
        JMenuItem saveProjectItem = new JMenuItem(languageBundle.getString("editor.menu.file.save"));
        saveProjectItem.addActionListener(e -> saveProject());
        fileMenu.add(saveProjectItem);
        
        fileMenu.addSeparator();
        
        JMenuItem nextProjectItem = new JMenuItem(languageBundle.getString("editor.menu.file.nextProject"));
        nextProjectItem.addActionListener(e -> switchToNextProject());
        fileMenu.add(nextProjectItem);
        
        JMenuItem previousProjectItem = new JMenuItem(languageBundle.getString("editor.menu.file.previousProject"));
        previousProjectItem.addActionListener(e -> switchToPreviousProject());
        fileMenu.add(previousProjectItem);
        
        fileMenu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem(languageBundle.getString("editor.menu.file.exit"));
        exitItem.addActionListener(e -> dispose());
        fileMenu.add(exitItem);
        
        menuBar.add(fileMenu);
        
        // Edit menu
        JMenu editMenu = new JMenu(languageBundle.getString("editor.menu.edit"));
        
        JMenuItem newActionItem = new JMenuItem(languageBundle.getString("editor.menu.edit.newAction"));
        newActionItem.addActionListener(e -> createNewAction());
        editMenu.add(newActionItem);
        
        JMenuItem deleteActionItem = new JMenuItem(languageBundle.getString("editor.menu.edit.deleteAction"));
        deleteActionItem.addActionListener(e -> deleteSelectedAction());
        editMenu.add(deleteActionItem);
        
        menuBar.add(editMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void setupEventHandlers() {
        // Project tree selection
        projectTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) projectTree.getLastSelectedPathComponent();
            if (selectedNode == null) return;
            
            Object userObject = selectedNode.getUserObject();
            if (userObject instanceof Project) {
                loadProject((Project) userObject);
            } else if (userObject instanceof AnimationAction) {
                selectAction((AnimationAction) userObject);
            }
        });
    }
    
    private void loadProjects() {
        try {
            File imgDirectory = new File("./img");
            availableProjects = ProjectScanner.scanProjects(imgDirectory);
            
            if (availableProjects.isEmpty()) {
                rootNode.removeAllChildren();
                treeModel.reload();
                JOptionPane.showMessageDialog(this, 
                    "No valid projects found in ./img directory",
                    "No Projects", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Load only the current project
            loadSingleProject(currentProjectIndex);
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to load projects", e);
            JOptionPane.showMessageDialog(this, 
                languageBundle.getString("editor.error.loadProjects") + ": " + e.getMessage(), 
                languageBundle.getString("editor.error.title"), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSingleProject(int projectIndex) {
        if (availableProjects == null || projectIndex >= availableProjects.size()) {
            return;
        }
        
        File projectDir = availableProjects.get(projectIndex);
        Project project = new Project(projectDir.getName(), projectDir);
        
        try {
            if (ProjectScanner.isValidProject(projectDir)) {
                // Load actions from XML
                List<AnimationAction> actions = XMLConfigUtil.loadActionsFromXML(
                    ProjectScanner.getConfigFile(projectDir));
                project.setActions(actions);
            }
            
            rootNode.removeAllChildren();
            
            DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(project);
            
            // Add action nodes
            for (AnimationAction action : project.getActions()) {
                DefaultMutableTreeNode actionNode = new DefaultMutableTreeNode(action);
                projectNode.add(actionNode);
            }
            
            rootNode.add(projectNode);
            treeModel.reload();
            expandTree();
            
            // Update current project reference
            this.currentProject = project;
            
            // Reset modification status when loading a project
            projectModified = false;
            
            // Update project directory for all panels
            updateProjectDirectoryForAllPanels();
            
            // Update window title to show current project
            updateWindowTitle();
            updateStatusLabel("Project loaded: " + project.getName());
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to load project: " + projectDir.getName(), e);
            updateStatusLabel("Failed to load project: " + projectDir.getName());
        }
    }
    
    private void switchToNextProject() {
        if (availableProjects != null && !availableProjects.isEmpty()) {
            // Clear preview first
            clearPreview();
            // Switch to next project
            currentProjectIndex = (currentProjectIndex + 1) % availableProjects.size();
            // Load the new project (this will update project directories)
            loadSingleProject(currentProjectIndex);
        }
    }

    private void switchToPreviousProject() {
        if (availableProjects != null && !availableProjects.isEmpty()) {
            // Clear preview first
            clearPreview();
            // Switch to previous project
            currentProjectIndex = (currentProjectIndex - 1 + availableProjects.size()) % availableProjects.size();
            // Load the new project (this will update project directories)
            loadSingleProject(currentProjectIndex);
        }
    }
    
    private void updateProjectDirectoryForAllPanels() {
        if (currentProject != null) {
            // Update project directory for preview panel
            if (previewPanel != null) {
                previewPanel.setProjectDirectory(currentProject.getRootDirectory());
            }
            // Update project directory for action details panel
            if (actionDetailsPanel != null) {
                actionDetailsPanel.setProjectDirectory(currentProject.getRootDirectory());
            }
        }
    }    private void clearPreview() {
        selectedAction = null;
        if (previewPanel != null) {
            previewPanel.setAction(null);
        }
        if (actionDetailsPanel != null) {
            actionDetailsPanel.setAction(null);
        }
    }
    
    private void expandTree() {
        for (int i = 0; i < projectTree.getRowCount(); i++) {
            projectTree.expandRow(i);
        }
    }
    
    private void loadProject(Project project) {
        this.currentProject = project;
        
        // Update current project index to match the selected project
        if (availableProjects != null) {
            for (int i = 0; i < availableProjects.size(); i++) {
                if (availableProjects.get(i).getName().equals(project.getName())) {
                    currentProjectIndex = i;
                    break;
                }
            }
        }
        
        // Set project directory for all panels
        updateProjectDirectoryForAllPanels();
        
        // Load images into grid
        imageGridPanel.removeAll();
        List<File> imageFiles = ProjectScanner.scanImageFiles(project.getRootDirectory());
        for (File imageFile : imageFiles) {
            addImageToGrid(imageFile);
        }
        imageGridPanel.revalidate();
        imageGridPanel.repaint();
        
        // Clear action selection and preview to avoid showing previous project data
        clearPreview();
        
        // Update window title
        updateWindowTitle();
    }
    
    private void selectAction(AnimationAction action) {
        this.selectedAction = action;
        
        // Make sure we're using the correct project context
        updateProjectDirectoryForAllPanels();
        
        actionDetailsPanel.setAction(action);
        previewPanel.setAction(action);
    }
    
    private void openProject() {
        JFileChooser chooser = new JFileChooser("./img");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle(languageBundle.getString("editor.dialog.selectProject"));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedDir = chooser.getSelectedFile();
            if (ProjectScanner.isValidProject(selectedDir)) {
                loadProjects(); // Reload all projects
            } else {
                JOptionPane.showMessageDialog(this, 
                    languageBundle.getString("editor.error.invalidProject"),
                    languageBundle.getString("editor.error.invalidProjectTitle"), JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void saveProject() {
        if (currentProject == null) {
            JOptionPane.showMessageDialog(this, 
                languageBundle.getString("editor.error.noProjectSelected"),
                languageBundle.getString("editor.error.noProjectTitle"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean success = XMLConfigUtil.saveActionsToXML(
            currentProject.getActions(), 
            currentProject.getConfigFile());
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                languageBundle.getString("editor.success.projectSaved"),
                languageBundle.getString("editor.success.saveTitle"), JOptionPane.INFORMATION_MESSAGE);
            markProjectAsSaved(); // Mark project as saved
        } else {
            JOptionPane.showMessageDialog(this, 
                languageBundle.getString("editor.error.saveFailed"),
                languageBundle.getString("editor.error.saveFailedTitle"), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void createNewAction() {
        if (currentProject == null) {
            JOptionPane.showMessageDialog(this, 
                languageBundle.getString("editor.error.noProjectSelected"),
                languageBundle.getString("editor.error.noProjectTitle"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String actionName = JOptionPane.showInputDialog(this, 
            languageBundle.getString("editor.dialog.enterActionName"), 
            languageBundle.getString("editor.dialog.newActionTitle"), JOptionPane.QUESTION_MESSAGE);
        
        if (actionName != null && !actionName.trim().isEmpty()) {
            AnimationAction newAction = new AnimationAction(actionName.trim(), "Stay");
            currentProject.getActions().add(newAction);
            
            // Refresh tree
            loadProjects();
            markProjectAsModified(); // Mark project as modified
        }
    }
    
    private void deleteSelectedAction() {
        if (selectedAction == null) {
            JOptionPane.showMessageDialog(this, 
                languageBundle.getString("editor.error.noActionSelected"),
                languageBundle.getString("editor.error.noSelectionTitle"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this,
            String.format(languageBundle.getString("editor.dialog.confirmDeleteAction"), selectedAction.getName()),
            languageBundle.getString("editor.dialog.confirmDeleteTitle"), JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            currentProject.getActions().remove(selectedAction);
            selectAction(null);
            loadProjects();
            markProjectAsModified(); // Mark project as modified
        }
    }
    
    private void createStatusBar() {
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Mark the project as modified and update UI accordingly
     */
    private void markProjectAsModified() {
        if (!projectModified) {
            projectModified = true;
            updateWindowTitle();
            updateStatusLabel("Project modified - remember to save changes");
        }
    }
    
    /**
     * Mark the project as saved and update UI accordingly
     */
    private void markProjectAsSaved() {
        if (projectModified) {
            projectModified = false;
            updateWindowTitle();
            updateStatusLabel("Project saved successfully");
        }
    }
    
    /**
     * Update the window title to show project name and modified status
     */
    private void updateWindowTitle() {
        String title = languageBundle.getString("editor.title");
        if (currentProject != null) {
            title += " - " + currentProject.getName();
            if (projectModified) {
                title += " *";
            }
        }
        setTitle(title);
    }
    
    /**
     * Update the status label with a message
     */
    private void updateStatusLabel(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
    
    /**
     * Add an image to the grid panel with preview
     */
    private void addImageToGrid(File imageFile) {
        // Create image panel using the renderer
        JPanel imagePanel = ImageGridPanelRenderer.createImagePanel(imageFile, 
            () -> {
                updateStatusLabel("选择了图片: " + imageFile.getName() + " / Selected image: " + imageFile.getName());
                // Remove highlight from all panels first
                for (Component comp : imageGridPanel.getComponents()) {
                    if (comp instanceof JPanel) {
                        comp.setBackground(Color.WHITE);
                        ((JPanel) comp).setBorder(BorderFactory.createRaisedBevelBorder());
                    }
                }
            }, 
            () -> {
                if (selectedAction != null) {
                    actionDetailsPanel.setImageForSelectedPose(imageFile.getAbsolutePath());
                    updateStatusLabel("设置图片到选中姿势: " + imageFile.getName() + " / Set image to selected pose: " + imageFile.getName());
                }
            });
        
        imageGridPanel.add(imagePanel);
    }
}
