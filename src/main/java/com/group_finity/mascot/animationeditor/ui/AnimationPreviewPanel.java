package com.group_finity.mascot.animationeditor.ui;

import com.group_finity.mascot.Main;
import com.group_finity.mascot.animationeditor.model.AnimationAction;
import com.group_finity.mascot.animationeditor.model.AnimationPose;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;

/**
 * 动画预览面板
 * Animation Preview Panel
 * 
 * @author DCShimeji Team
 */
public class AnimationPreviewPanel extends JPanel {
    private ResourceBundle languageBundle;
    private AnimationAction currentAction;
    private Timer animationTimer;
    private int currentPoseIndex = 0;
    private int currentFrameCount = 0;
    private boolean isPlaying = false;
    private boolean isSlowMotion = false;
    
    // Current project directory for image loading
    private File currentProjectDirectory;
    
    // UI Components
    private JPanel previewArea;
    private JLabel imageLabel;
    private JPanel controlPanel;
    private JButton playButton;
    private JButton stopButton;
    private JButton slowMotionButton;
    private JLabel frameInfoLabel;
    private JProgressBar progressBar;
    
    public AnimationPreviewPanel() {
        languageBundle = Main.getInstance().getLanguageBundle();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupAnimation();
    }
    
    private void initializeComponents() {
        // Preview area
        previewArea = new JPanel(new BorderLayout());
        previewArea.setBackground(Color.LIGHT_GRAY);
        previewArea.setPreferredSize(new Dimension(300, 200));
        previewArea.setBorder(BorderFactory.createLoweredBevelBorder());
        
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setText(languageBundle.getString("editor.preview.selectAnimation"));
        
        // Control panel
        controlPanel = new JPanel(new FlowLayout());
        
        playButton = new JButton(languageBundle.getString("editor.preview.play"));
        playButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        
        stopButton = new JButton(languageBundle.getString("editor.preview.stop"));
        stopButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        
        slowMotionButton = new JButton(languageBundle.getString("editor.preview.slowPlay"));
        slowMotionButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        
        frameInfoLabel = new JLabel(languageBundle.getString("editor.preview.frameInfo") + " 0/0");
        frameInfoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("0%");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Preview area
        previewArea.add(imageLabel, BorderLayout.CENTER);
        add(previewArea, BorderLayout.CENTER);
        
        // Control panel
        controlPanel.add(playButton);
        controlPanel.add(stopButton);
        controlPanel.add(slowMotionButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(frameInfoLabel);
        
        // Bottom panel with progress bar
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(controlPanel, BorderLayout.CENTER);
        bottomPanel.add(progressBar, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        playButton.addActionListener(e -> {
            if (currentAction != null && !currentAction.getPoses().isEmpty()) {
                startAnimation(false);
            }
        });
        
        stopButton.addActionListener(e -> stopAnimation());
        
        slowMotionButton.addActionListener(e -> {
            if (currentAction != null && !currentAction.getPoses().isEmpty()) {
                startAnimation(true);
            }
        });
    }
    
    private void setupAnimation() {
        // Create timer for animation playback
        animationTimer = new Timer(40, e -> updateFrame()); // 25 FPS by default
    }
    
    public void setAction(AnimationAction action) {
        stopAnimation();
        this.currentAction = action;
        currentPoseIndex = 0;
        currentFrameCount = 0;
        updateDisplay();
        updateControls();
    }
    
    public void setProjectDirectory(File projectDirectory) {
        this.currentProjectDirectory = projectDirectory;
        // Refresh display if we have a current action
        if (currentAction != null) {
            updateDisplay();
        }
    }
    
    private void startAnimation(boolean slowMotion) {
        if (currentAction == null || currentAction.getPoses().isEmpty()) {
            return;
        }
        
        stopAnimation();
        isSlowMotion = slowMotion;
        isPlaying = true;
        currentPoseIndex = 0;
        currentFrameCount = 0;
        
        // Set timer delay based on slow motion mode
        int delay = slowMotion ? 400 : 40; // 10x slower for slow motion
        animationTimer.setDelay(delay);
        animationTimer.start();
        
        updateControls();
    }
    
    private void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        isPlaying = false;
        isSlowMotion = false;
        currentPoseIndex = 0;
        currentFrameCount = 0;
        updateDisplay();
        updateControls();
    }
    
    private void updateFrame() {
        if (currentAction == null || currentAction.getPoses().isEmpty()) {
            stopAnimation();
            return;
        }
        
        List<AnimationPose> poses = currentAction.getPoses();
        AnimationPose currentPose = poses.get(currentPoseIndex);
        
        currentFrameCount++;
        
        // Check if we should move to next pose
        int frameDuration = isSlowMotion ? currentPose.getDuration() / 10 : currentPose.getDuration();
        if (frameDuration <= 0) frameDuration = 1;
        
        if (currentFrameCount >= frameDuration) {
            currentFrameCount = 0;
            currentPoseIndex = (currentPoseIndex + 1) % poses.size();
        }
        
        updateDisplay();
    }
    
    private void updateDisplay() {
        if (currentAction == null || currentAction.getPoses().isEmpty()) {
            imageLabel.setIcon(null);
            imageLabel.setText(languageBundle.getString("editor.preview.selectAnimation"));
            frameInfoLabel.setText(languageBundle.getString("editor.preview.frameInfo") + " 0/0");
            progressBar.setValue(0);
            progressBar.setString("0%");
            return;
        }

        List<AnimationPose> poses = currentAction.getPoses();
        AnimationPose currentPose = poses.get(currentPoseIndex);
        
        // Load and display current pose image
        loadAndDisplayImage(currentPose);
        
        // Update frame info
        frameInfoLabel.setText(String.format(languageBundle.getString("editor.preview.frameInfo") + " %d/%d", 
            currentPoseIndex + 1, poses.size()));
        
        // Update progress bar
        int progress = !poses.isEmpty() ? (currentPoseIndex * 100 / poses.size()) : 0;
        progressBar.setValue(progress);
        progressBar.setString(progress + "%");
    }    private void loadAndDisplayImage(AnimationPose pose) {
        if (pose == null || pose.getImage() == null) {
            imageLabel.setIcon(null);
            imageLabel.setText(languageBundle.getString("editor.pose.noImage"));
            return;
        }

        try {
            // Try to load image from project directory
            String imagePath = pose.getImage();
            if (imagePath.startsWith("/")) {
                imagePath = imagePath.substring(1);
            }
            
            // Search for image in current project directory
            File imageFile = findImageFile(imagePath);
            
            if (imageFile != null && imageFile.exists()) {
                BufferedImage image = ImageIO.read(imageFile);
                if (image != null) {
                    // Scale image to fit preview area while maintaining aspect ratio
                    ImageIcon scaledIcon = scaleImageToFit(image, 280, 180);
                    imageLabel.setIcon(scaledIcon);
                    imageLabel.setText("");
                } else {
                    imageLabel.setIcon(null);
                    imageLabel.setText(languageBundle.getString("editor.preview.cannotLoadImage"));
                }
            } else {
                imageLabel.setIcon(null);
                imageLabel.setText(languageBundle.getString("editor.preview.imageNotFound") + ": " + imagePath);
            }
        } catch (Exception e) {
            imageLabel.setIcon(null);
            imageLabel.setText(languageBundle.getString("editor.preview.loadImageFailed"));
        }
    }
    
    private File findImageFile(String imageName) {
        // Use current project directory if available, otherwise fall back to ./img
        File searchDir = currentProjectDirectory != null ? currentProjectDirectory : new File("./img");
        
        if (!searchDir.exists()) return null;
        
        // Search recursively for the image file in the project directory
        return findFileRecursively(searchDir, imageName);
    }
    
    private File findFileRecursively(File directory, String fileName) {
        if (!directory.isDirectory()) return null;
        
        File[] files = directory.listFiles();
        if (files == null) return null;
        
        for (File file : files) {
            if (file.isFile() && file.getName().equals(fileName)) {
                return file;
            } else if (file.isDirectory()) {
                File found = findFileRecursively(file, fileName);
                if (found != null) return found;
            }
        }
        return null;
    }
    
    private ImageIcon scaleImageToFit(BufferedImage image, int maxWidth, int maxHeight) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        // Calculate scaling factor
        double scaleX = (double) maxWidth / width;
        double scaleY = (double) maxHeight / height;
        double scale = Math.min(scaleX, scaleY);
        
        if (scale >= 1.0) {
            // Don't upscale
            return new ImageIcon(image);
        }
        
        int scaledWidth = (int) (width * scale);
        int scaledHeight = (int) (height * scale);
        
        Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
    
    private void updateControls() {
        boolean hasAnimation = currentAction != null && !currentAction.getPoses().isEmpty();
        playButton.setEnabled(hasAnimation && !isPlaying);
        stopButton.setEnabled(hasAnimation && isPlaying);
        slowMotionButton.setEnabled(hasAnimation && !isPlaying);
        
        if (isPlaying) {
            if (isSlowMotion) {
                playButton.setText(languageBundle.getString("editor.preview.playingSlow"));
                slowMotionButton.setText(languageBundle.getString("editor.preview.slowPlaying"));
            } else {
                playButton.setText(languageBundle.getString("editor.preview.playing"));
                slowMotionButton.setText(languageBundle.getString("editor.preview.slowPlay"));
            }
        } else {
            playButton.setText(languageBundle.getString("editor.preview.play"));
            slowMotionButton.setText(languageBundle.getString("editor.preview.slowPlay"));
        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        playButton.setEnabled(enabled);
        stopButton.setEnabled(enabled);
        slowMotionButton.setEnabled(enabled);
    }
}
