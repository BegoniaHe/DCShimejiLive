package com.group_finity.mascot.animationeditor.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * 动画动作模型
 * Animation Action Model
 * 
 * @author DCShimeji Team
 */
public class AnimationAction {
    private String name;
    private String type;
    private String className;
    private String borderType;
    private Map<String, String> attributes;
    private List<AnimationPose> poses;
    
    public AnimationAction() {
        this.attributes = new HashMap<>();
        this.poses = new ArrayList<>();
    }
    
    public AnimationAction(String name, String type) {
        this();
        this.name = name;
        this.type = type;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    
    public String getBorderType() { return borderType; }
    public void setBorderType(String borderType) { this.borderType = borderType; }
    
    public Map<String, String> getAttributes() { return attributes; }
    public void setAttributes(Map<String, String> attributes) { this.attributes = attributes; }
    
    public List<AnimationPose> getPoses() { return poses; }
    public void setPoses(List<AnimationPose> poses) { this.poses = poses; }
    
    public void addPose(AnimationPose pose) {
        this.poses.add(pose);
    }
    
    public void removePose(AnimationPose pose) {
        this.poses.remove(pose);
    }
    
    public void setAttribute(String key, String value) {
        this.attributes.put(key, value);
    }
    
    public String getAttribute(String key) {
        return this.attributes.get(key);
    }
    
    @Override
    public String toString() {
        return name != null ? name : "Unnamed Action";
    }
}
