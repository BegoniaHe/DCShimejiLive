package com.group_finity.mascot.animationeditor.ui;

import com.group_finity.mascot.Main;
import com.group_finity.mascot.animationeditor.model.AnimationPose;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 姿势详情面板
 * Pose Details Panel
 * 
 * @author DCShimeji Team
 */
public class PoseDetailsPanel extends JPanel {
    private AnimationPose currentPose;
    private List<Runnable> changeListeners = new ArrayList<>();
    private ResourceBundle languageBundle;
    
    // Current project directory for file operations
    private File currentProjectDirectory;
    
    // Components
    private JTextField imageField;
    private JButton browseImageButton;
    private JTextField imageAnchorField;
    private JTextField velocityField;
    private JSpinner durationSpinner;
    private JTextField soundField;
    private JButton browseSoundButton;
    private JSlider volumeSlider;
    private JLabel volumeLabel;
    
    public PoseDetailsPanel() {
        languageBundle = Main.getInstance().getLanguageBundle();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setPose(null);
    }
    
    private void initializeComponents() {
        imageField = new JTextField();
        imageField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        browseImageButton = new JButton(languageBundle.getString("editor.button.browse"));
        browseImageButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        
        imageAnchorField = new JTextField();
        imageAnchorField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        imageAnchorField.setToolTipText("Format: x,y (e.g., 64,128) - defines the anchor point of the image");
        
        velocityField = new JTextField();
        velocityField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        velocityField.setToolTipText("Format: x,y (e.g., 5,0) - defines the movement velocity in pixels");
        
        durationSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        durationSpinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        
        soundField = new JTextField();
        soundField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        browseSoundButton = new JButton(languageBundle.getString("editor.button.browse"));
        browseSoundButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        
        volumeSlider = new JSlider(0, 100, 100);
        volumeLabel = new JLabel("100%");
        volumeLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        
        volumeSlider.addChangeListener(e -> {
            int value = volumeSlider.getValue();
            volumeLabel.setText(value + "%");
        });
    }
    
    private void setupLayout() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(languageBundle.getString("editor.panel.poseDetails")));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        Font labelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
        
        // Image
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel imageLabel = new JLabel(languageBundle.getString("editor.label.image"));
        imageLabel.setFont(labelFont);
        add(imageLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        add(imageField, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        add(browseImageButton, gbc);
        
        // Image Anchor
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel anchorLabel = new JLabel(languageBundle.getString("editor.label.imageAnchor"));
        anchorLabel.setFont(labelFont);
        add(anchorLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        add(imageAnchorField, gbc);
        
        // Velocity
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        JLabel velocityLabel = new JLabel(languageBundle.getString("editor.label.velocity"));
        velocityLabel.setFont(labelFont);
        add(velocityLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        add(velocityField, gbc);
        
        // Duration
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        JLabel durationLabel = new JLabel(languageBundle.getString("editor.label.durationFrames"));
        durationLabel.setFont(labelFont);
        add(durationLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        add(durationSpinner, gbc);
        
        // Duration explanation
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel durationExplanation = new JLabel(languageBundle.getString("editor.label.durationTip"));
        durationExplanation.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 11));
        durationExplanation.setForeground(Color.GRAY);
        add(durationExplanation, gbc);
        
        // Sound
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        JLabel soundLabel = new JLabel(languageBundle.getString("editor.label.sound"));
        soundLabel.setFont(labelFont);
        add(soundLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        add(soundField, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        add(browseSoundButton, gbc);
        
        // Volume
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1;
        JLabel volumeSliderLabel = new JLabel(languageBundle.getString("editor.label.volume"));
        volumeSliderLabel.setFont(labelFont);
        add(volumeSliderLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        add(volumeSlider, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        add(volumeLabel, gbc);
    }
    
    private void setupEventHandlers() {
        // Add both ActionListener and FocusListener for immediate updates
        imageField.addActionListener(e -> updatePoseFromUI());
        imageField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                updatePoseFromUI();
            }
        });
        
        imageAnchorField.addActionListener(e -> updatePoseFromUI());
        imageAnchorField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                updatePoseFromUI();
            }
        });
        
        velocityField.addActionListener(e -> updatePoseFromUI());
        velocityField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                updatePoseFromUI();
            }
        });
        
        durationSpinner.addChangeListener(e -> updatePoseFromUI());
        
        soundField.addActionListener(e -> updatePoseFromUI());
        soundField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                updatePoseFromUI();
            }
        });
        
        volumeSlider.addChangeListener(e -> updatePoseFromUI());
        
        browseImageButton.addActionListener(e -> browseForImage());
        browseSoundButton.addActionListener(e -> browseForSound());
    }
    
    public void setPose(AnimationPose pose) {
        this.currentPose = pose;
        updateUIFromPose();
    }
    
    private void updateUIFromPose() {
        if (currentPose == null) {
            // Clear all fields
            imageField.setText("");
            imageAnchorField.setText("");
            velocityField.setText("");
            durationSpinner.setValue(1);
            soundField.setText("");
            volumeSlider.setValue(100);
            volumeLabel.setText("100%");
            
            // Disable all controls
            setEnabled(false);
        } else {
            // Populate fields
            imageField.setText(currentPose.getImage() != null ? currentPose.getImage() : "");
            imageAnchorField.setText(currentPose.getImageAnchor() != null ? currentPose.getImageAnchor() : "");
            velocityField.setText(currentPose.getVelocity() != null ? currentPose.getVelocity() : "");
            durationSpinner.setValue(currentPose.getDuration());
            soundField.setText(currentPose.getSound() != null ? currentPose.getSound() : "");
            
            int volumePercent = (int) (currentPose.getVolume() * 100);
            volumeSlider.setValue(volumePercent);
            volumeLabel.setText(volumePercent + "%");
            
            // Enable controls
            setEnabled(true);
        }
    }
    
    private void updatePoseFromUI() {
        if (currentPose == null) return;
        
        // Update image path
        currentPose.setImage(imageField.getText().trim());
        
        // Validate and update image anchor (should be "x,y" format)
        String anchorText = imageAnchorField.getText().trim();
        if (anchorText.isEmpty()) {
            currentPose.setImageAnchor("0,0"); // Default value
        } else if (isValidCoordinate(anchorText)) {
            currentPose.setImageAnchor(anchorText);
        } else {
            // Reset to previous valid value or default
            imageAnchorField.setText(currentPose.getImageAnchor() != null ? currentPose.getImageAnchor() : "0,0");
            showValidationMessage("Image Anchor should be in format 'x,y' (e.g., '64,128')");
            return;
        }
        
        // Validate and update velocity (should be "x,y" format)
        String velocityText = velocityField.getText().trim();
        if (velocityText.isEmpty()) {
            currentPose.setVelocity("0,0"); // Default value
        } else if (isValidCoordinate(velocityText)) {
            currentPose.setVelocity(velocityText);
        } else {
            // Reset to previous valid value or default
            velocityField.setText(currentPose.getVelocity() != null ? currentPose.getVelocity() : "0,0");
            showValidationMessage("Velocity should be in format 'x,y' (e.g., '5,0')");
            return;
        }
        
        // Update other fields
        currentPose.setDuration((Integer) durationSpinner.getValue());
        currentPose.setSound(soundField.getText().trim());
        currentPose.setVolume(volumeSlider.getValue() / 100.0);
        
        // Notify change listeners
        for (Runnable listener : changeListeners) {
            listener.run();
        }
    }
    
    /**
     * Validate coordinate format (x,y where x and y are numbers)
     */
    private boolean isValidCoordinate(String coordinate) {
        if (coordinate == null || coordinate.trim().isEmpty()) {
            return true; // Empty is valid, will use default
        }
        
        String[] parts = coordinate.split(",");
        if (parts.length != 2) {
            return false;
        }
        
        try {
            Double.parseDouble(parts[0].trim());
            Double.parseDouble(parts[1].trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Show validation message to user
     */
    private void showValidationMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Validation", JOptionPane.WARNING_MESSAGE);
    }
    
    private void browseForImage() {
        // Use current project directory if available, otherwise use ./img
        String initialDir = currentProjectDirectory != null ? 
            currentProjectDirectory.getAbsolutePath() : "./img";
        
        JFileChooser chooser = new JFileChooser(initialDir);
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            languageBundle.getString("editor.filter.imageFiles"), "png", "jpg", "jpeg", "gif", "bmp"));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String imagePath = "/" + chooser.getSelectedFile().getName();
            imageField.setText(imagePath);
            updatePoseFromUI();
        }
    }
    
    private void browseForSound() {
        // Use current project directory if available, otherwise use ./sound
        String initialDir = currentProjectDirectory != null ? 
            currentProjectDirectory.getAbsolutePath() + "/sound" : "./sound";
        
        JFileChooser chooser = new JFileChooser(initialDir);
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            languageBundle.getString("editor.filter.audioFiles"), "wav", "mp3", "ogg"));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String soundPath = "/" + chooser.getSelectedFile().getName();
            soundField.setText(soundPath);
            updatePoseFromUI();
        }
    }
    
    public void addChangeListener(Runnable listener) {
        changeListeners.add(listener);
    }
    
    public void removeChangeListener(Runnable listener) {
        changeListeners.remove(listener);
    }
    
    public void setProjectDirectory(File projectDirectory) {
        this.currentProjectDirectory = projectDirectory;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        imageField.setEnabled(enabled);
        browseImageButton.setEnabled(enabled);
        imageAnchorField.setEnabled(enabled);
        velocityField.setEnabled(enabled);
        durationSpinner.setEnabled(enabled);
        soundField.setEnabled(enabled);
        browseSoundButton.setEnabled(enabled);
        volumeSlider.setEnabled(enabled);
    }
}
