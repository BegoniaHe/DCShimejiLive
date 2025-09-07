package com.group_finity.mascot.animationeditor.ui;

import com.group_finity.mascot.Main;
import com.group_finity.mascot.animationeditor.model.AnimationAction;
import com.group_finity.mascot.animationeditor.model.AnimationPose;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.util.ResourceBundle;

/**
 * 动作详情面板 - 简洁版本
 * Action Details Panel - Simplified Version
 * 
 * @author DCShimeji Team
 */
public class ActionDetailsPanel extends JPanel {
    private AnimationAction currentAction;
    private ResourceBundle languageBundle;
    
    // Current project directory for file operations
    private File currentProjectDirectory;
    
    // Change listeners for notifying parent components about modifications
    private java.util.List<Runnable> changeListeners = new java.util.ArrayList<>();
    
    // Action basic info components
    private JTextField nameField;
    private JComboBox<String> typeComboBox;
    private JTextField classField;
    private JComboBox<String> borderTypeComboBox;
    
    // Poses list - simplified to show just names
    private JList<AnimationPose> posesList;
    private DefaultListModel<AnimationPose> posesListModel;
    
    // Action buttons
    private JButton addPoseButton;
    private JButton removePoseButton;
    private JButton moveUpButton;
    private JButton moveDownButton;
    
    // Pose details (shown when a pose is selected)
    private PoseDetailsPanel poseDetailsPanel;
    
    public ActionDetailsPanel() {
        languageBundle = Main.getInstance().getLanguageBundle();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setAction(null); // Initialize with empty state
    }
    
    private void initializeComponents() {
        // Basic info components
        nameField = new JTextField();
        nameField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        
        typeComboBox = new JComboBox<>(new String[]{
            "Stay", "Move", "Animate", "Sequence", "Select", "Embedded"
        });
        typeComboBox.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        
        classField = new JTextField();
        classField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        
        borderTypeComboBox = new JComboBox<>(new String[]{
            "", "Floor", "Wall", "Ceiling"
        });
        borderTypeComboBox.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        
        // Poses list - simplified renderer
        posesListModel = new DefaultListModel<>();
        posesList = new JList<>(posesListModel);
        posesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        posesList.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        posesList.setCellRenderer(new SimplePoseListRenderer());
        
        // Pose details panel
        poseDetailsPanel = new PoseDetailsPanel();
        
        // Action buttons - user-friendly design
        addPoseButton = new JButton(languageBundle.getString("editor.button.addPose"));
        addPoseButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        addPoseButton.setToolTipText(languageBundle.getString("editor.button.addPose.tooltip"));
        
        removePoseButton = new JButton(languageBundle.getString("editor.button.removePose"));
        removePoseButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        removePoseButton.setToolTipText(languageBundle.getString("editor.button.removePose.tooltip"));
        
        moveUpButton = new JButton(languageBundle.getString("editor.button.moveUp"));
        moveUpButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        moveUpButton.setToolTipText(languageBundle.getString("editor.button.moveUp.tooltip"));
        
        moveDownButton = new JButton(languageBundle.getString("editor.button.moveDown"));
        moveDownButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        moveDownButton.setToolTipText(languageBundle.getString("editor.button.moveDown.tooltip"));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        // Top panel - basic action info
        JPanel topPanel = createBasicInfoPanel();
        
        // Center panel - poses list and controls
        JPanel centerPanel = createPosesPanel();
        
        // Bottom panel - pose details
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), languageBundle.getString("editor.panel.poseDetails"), 
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
            new Font(Font.SANS_SERIF, Font.BOLD, 16)));
        bottomPanel.add(poseDetailsPanel, BorderLayout.CENTER);
        
        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), languageBundle.getString("editor.panel.basicInfo"), 
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
            new Font(Font.SANS_SERIF, Font.BOLD, 16)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        
        Font labelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel(languageBundle.getString("editor.label.name"));
        nameLabel.setFont(labelFont);
        panel.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(nameField, gbc);
        
        // Type
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel typeLabel = new JLabel(languageBundle.getString("editor.label.type"));
        typeLabel.setFont(labelFont);
        panel.add(typeLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(typeComboBox, gbc);
        
        // Class (optional)
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel classLabel = new JLabel(languageBundle.getString("editor.label.class"));
        classLabel.setFont(labelFont);
        panel.add(classLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(classField, gbc);
        
        // Border Type
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel borderLabel = new JLabel(languageBundle.getString("editor.label.border"));
        borderLabel.setFont(labelFont);
        panel.add(borderLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(borderTypeComboBox, gbc);
        
        return panel;
    }
    
    private JPanel createPosesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), languageBundle.getString("editor.panel.animationPoses"), 
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
            new Font(Font.SANS_SERIF, Font.BOLD, 16)));
        
        // Poses list
        JScrollPane scrollPane = new JScrollPane(posesList);
        scrollPane.setPreferredSize(new Dimension(0, 100)); // 减小高度
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(addPoseButton);
        buttonsPanel.add(removePoseButton);
        buttonsPanel.add(moveUpButton);
        buttonsPanel.add(moveDownButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        // Text field listeners - add both ActionListener and FocusListener for real-time updates
        nameField.addActionListener(e -> updateActionFromUI());
        nameField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                updateActionFromUI();
            }
        });
        
        typeComboBox.addActionListener(e -> updateActionFromUI());
        
        classField.addActionListener(e -> updateActionFromUI());
        classField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                updateActionFromUI();
            }
        });
        
        borderTypeComboBox.addActionListener(e -> updateActionFromUI());
        
        // Poses list selection
        posesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                AnimationPose selectedPose = posesList.getSelectedValue();
                poseDetailsPanel.setPose(selectedPose);
                updateButtonStates();
            }
        });
        
        // Button listeners
        addPoseButton.addActionListener(e -> addNewPose());
        removePoseButton.addActionListener(e -> removeSelectedPose());
        moveUpButton.addActionListener(e -> movePoseUp());
        moveDownButton.addActionListener(e -> movePoseDown());
        
        // Pose details change listener
        poseDetailsPanel.addChangeListener(() -> {
            // Refresh the pose list to show updated names
            posesList.repaint();
            // Notify that the action has been modified through pose changes
            notifyChangeListeners();
        });
    }
    
    public void setAction(AnimationAction action) {
        this.currentAction = action;
        updateUIFromAction();
    }
    
    public void setProjectDirectory(File projectDirectory) {
        this.currentProjectDirectory = projectDirectory;
        // Also set it for the pose details panel
        if (poseDetailsPanel != null) {
            poseDetailsPanel.setProjectDirectory(projectDirectory);
        }
    }
    
    private void updateUIFromAction() {
        if (currentAction == null) {
            // Clear all fields
            nameField.setText("");
            typeComboBox.setSelectedIndex(0);
            classField.setText("");
            borderTypeComboBox.setSelectedIndex(0);
            posesListModel.clear();
            poseDetailsPanel.setPose(null);
            
            // Disable all controls
            setControlsEnabled(false);
        } else {
            // Populate fields
            nameField.setText(currentAction.getName() != null ? currentAction.getName() : "");
            typeComboBox.setSelectedItem(currentAction.getType() != null ? currentAction.getType() : "Stay");
            classField.setText(currentAction.getClassName() != null ? currentAction.getClassName() : "");
            borderTypeComboBox.setSelectedItem(currentAction.getBorderType() != null ? currentAction.getBorderType() : "");
            
            // Populate poses list
            posesListModel.clear();
            for (AnimationPose pose : currentAction.getPoses()) {
                posesListModel.addElement(pose);
            }
            
            // Enable controls
            setControlsEnabled(true);
        }
        
        updateButtonStates();
    }
    
    private void updateActionFromUI() {
        if (currentAction == null) return;
        
        currentAction.setName(nameField.getText());
        currentAction.setType((String) typeComboBox.getSelectedItem());
        currentAction.setClassName(classField.getText());
        currentAction.setBorderType((String) borderTypeComboBox.getSelectedItem());
        
        // Notify listeners that the action has been modified
        notifyChangeListeners();
    }
    
    private void addNewPose() {
        if (currentAction == null) return;
        
        AnimationPose newPose = new AnimationPose();
        newPose.setImage("/Stand.png");
        newPose.setImageAnchor("64,128");
        newPose.setVelocity("0,0");
        newPose.setDuration(25); // 25 frames = 1 second (25 * 40ms)
        
        currentAction.addPose(newPose);
        posesListModel.addElement(newPose);
        
        // Select the new pose
        posesList.setSelectedValue(newPose, true);
        
        // Notify listeners that the action has been modified
        notifyChangeListeners();
    }
    
    private void removeSelectedPose() {
        AnimationPose selectedPose = posesList.getSelectedValue();
        if (selectedPose != null && currentAction != null) {
            currentAction.removePose(selectedPose);
            posesListModel.removeElement(selectedPose);
            poseDetailsPanel.setPose(null);
            
            // Notify listeners that the action has been modified
            notifyChangeListeners();
        }
    }
    
    private void movePoseUp() {
        int selectedIndex = posesList.getSelectedIndex();
        if (selectedIndex > 0) {
            AnimationPose pose = posesListModel.remove(selectedIndex);
            posesListModel.add(selectedIndex - 1, pose);
            posesList.setSelectedIndex(selectedIndex - 1);
            
            // Update the action's poses list as well
            currentAction.getPoses().remove(selectedIndex);
            currentAction.getPoses().add(selectedIndex - 1, pose);
            
            // Notify listeners that the action has been modified
            notifyChangeListeners();
        }
    }
    
    private void movePoseDown() {
        int selectedIndex = posesList.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < posesListModel.size() - 1) {
            AnimationPose pose = posesListModel.remove(selectedIndex);
            posesListModel.add(selectedIndex + 1, pose);
            posesList.setSelectedIndex(selectedIndex + 1);
            
            // Update the action's poses list as well
            currentAction.getPoses().remove(selectedIndex);
            currentAction.getPoses().add(selectedIndex + 1, pose);
            
            // Notify listeners that the action has been modified
            notifyChangeListeners();
        }
    }
    
    private void updateButtonStates() {
        boolean hasAction = currentAction != null;
        boolean hasSelectedPose = posesList.getSelectedValue() != null;
        int selectedIndex = posesList.getSelectedIndex();
        
        addPoseButton.setEnabled(hasAction);
        removePoseButton.setEnabled(hasSelectedPose);
        moveUpButton.setEnabled(hasSelectedPose && selectedIndex > 0);
        moveDownButton.setEnabled(hasSelectedPose && selectedIndex < posesListModel.size() - 1);
    }
    
    private void setControlsEnabled(boolean enabled) {
        nameField.setEnabled(enabled);
        typeComboBox.setEnabled(enabled);
        classField.setEnabled(enabled);
        borderTypeComboBox.setEnabled(enabled);
        posesList.setEnabled(enabled);
        poseDetailsPanel.setEnabled(enabled);
        updateButtonStates();
    }
    
    // Method to set image for selected pose (called from main window)
    public void setImageForSelectedPose(String imagePath) {
        AnimationPose selectedPose = posesList.getSelectedValue();
        if (selectedPose != null) {
            selectedPose.setImage(imagePath);
            poseDetailsPanel.setPose(selectedPose); // Refresh the details panel
            posesList.repaint(); // Refresh the list display
        }
    }
    
    /**
     * Simple pose list cell renderer - just shows image name
     */
    private class SimplePoseListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof AnimationPose) {
                AnimationPose pose = (AnimationPose) value;
                String text = String.format(languageBundle.getString("editor.pose.listFormat"), 
                    index + 1, 
                    pose.getImage() != null ? pose.getImage() : languageBundle.getString("editor.pose.noImage"),
                    pose.getDuration());
                setText(text);
            }
            
            return this;
        }
    }
    
    /**
     * Add a change listener that will be notified when action data is modified
     */
    public void addChangeListener(Runnable listener) {
        changeListeners.add(listener);
    }
    
    /**
     * Remove a change listener
     */
    public void removeChangeListener(Runnable listener) {
        changeListeners.remove(listener);
    }
    
    /**
     * Notify all change listeners that the action has been modified
     */
    private void notifyChangeListeners() {
        for (Runnable listener : changeListeners) {
            listener.run();
        }
    }
}
