package com.group_finity.mascot.animationeditor.model;

/**
 * 动画姿势模型
 * Animation Pose Model
 * 
 * @author DCShimeji Team
 */
public class AnimationPose {
    private String image;
    private String imageAnchor;
    private String velocity;
    private int duration;
    private String sound;
    private double volume = 1.0;
    
    public AnimationPose() {
        this.duration = 1;
    }
    
    public AnimationPose(String image, String imageAnchor, String velocity, int duration) {
        this.image = image;
        this.imageAnchor = imageAnchor;
        this.velocity = velocity;
        this.duration = duration;
    }
    
    // Getters and Setters
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    public String getImageAnchor() { return imageAnchor; }
    public void setImageAnchor(String imageAnchor) { this.imageAnchor = imageAnchor; }
    
    public String getVelocity() { return velocity; }
    public void setVelocity(String velocity) { this.velocity = velocity; }
    
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    
    public String getSound() { return sound; }
    public void setSound(String sound) { this.sound = sound; }
    
    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = volume; }
    
    @Override
    public String toString() {
        return image != null ? image : "No Image";
    }
}
