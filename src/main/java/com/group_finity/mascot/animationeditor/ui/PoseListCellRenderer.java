package com.group_finity.mascot.animationeditor.ui;

import com.group_finity.mascot.animationeditor.model.AnimationPose;

import javax.swing.*;
import java.awt.*;

/**
 * 姿势列表单元格渲染器 - 显示详细的帧信息
 * Pose List Cell Renderer - Shows detailed frame information
 * 
 * @author DCShimeji Team
 */
public class PoseListCellRenderer extends DefaultListCellRenderer {
    
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        
        if (value instanceof AnimationPose) {
            AnimationPose pose = (AnimationPose) value;
            
            // Create a multi-line label
            String htmlText = "<html>" +
                "<b>帧 " + (index + 1) + " / Frame " + (index + 1) + "</b><br/>" +
                "图片 / Image: " + (pose.getImage() != null ? pose.getImage() : "无 / None") + "<br/>" +
                "锚点 / Anchor: " + (pose.getImageAnchor() != null ? pose.getImageAnchor() : "0,0") + "<br/>" +
                "速度 / Velocity: " + (pose.getVelocity() != null ? pose.getVelocity() : "0,0") + "<br/>" +
                "持续时间 / Duration: " + pose.getDuration() +
                (pose.getSound() != null && !pose.getSound().isEmpty() ? 
                    "<br/>音效 / Sound: " + pose.getSound() : "") +
                "</html>";
            
            setText(htmlText);
        } else {
            setText(value != null ? value.toString() : "");
        }
        
        // Apply selection styling
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        return this;
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        // Make cells taller to accommodate multi-line text
        size.height = Math.max(size.height, 80);
        return size;
    }
}
