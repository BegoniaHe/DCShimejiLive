package com.group_finity.mascot.animationeditor.model;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * 动画项目模型
 * Animation Project Model
 * 
 * @author DCShimeji Team
 */
public class Project {
    private String name;
    private File rootDirectory;
    private File configFile;
    private List<AnimationAction> actions;
    private List<File> imageFiles;
    
    public Project(String name, File rootDirectory) {
        this.name = name;
        this.rootDirectory = rootDirectory;
        this.configFile = new File(rootDirectory, "conf/actions.xml");
        this.actions = new ArrayList<>();
        this.imageFiles = new ArrayList<>();
        loadImageFiles();
    }
    
    private void loadImageFiles() {
        if (rootDirectory.exists() && rootDirectory.isDirectory()) {
            File[] files = rootDirectory.listFiles((dir, name) -> {
                String lowerName = name.toLowerCase();
                return lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || 
                       lowerName.endsWith(".jpeg") || lowerName.endsWith(".gif");
            });
            
            if (files != null) {
                for (File file : files) {
                    imageFiles.add(file);
                }
            }
        }
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public File getRootDirectory() { return rootDirectory; }
    public void setRootDirectory(File rootDirectory) { this.rootDirectory = rootDirectory; }
    
    public File getConfigFile() { return configFile; }
    public void setConfigFile(File configFile) { this.configFile = configFile; }
    
    public List<AnimationAction> getActions() { return actions; }
    public void setActions(List<AnimationAction> actions) { this.actions = actions; }
    
    public List<File> getImageFiles() { return imageFiles; }
    public void setImageFiles(List<File> imageFiles) { this.imageFiles = imageFiles; }
    
    @Override
    public String toString() {
        return name;
    }
}
