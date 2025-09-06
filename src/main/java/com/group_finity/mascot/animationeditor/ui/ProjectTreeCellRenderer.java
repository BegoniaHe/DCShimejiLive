package com.group_finity.mascot.animationeditor.ui;

import com.group_finity.mascot.animationeditor.model.Project;
import com.group_finity.mascot.animationeditor.model.AnimationAction;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * 项目树单元格渲染器
 * Project Tree Cell Renderer
 * 
 * @author DCShimeji Team
 */
public class ProjectTreeCellRenderer extends DefaultTreeCellRenderer {
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        
        if (value instanceof javax.swing.tree.DefaultMutableTreeNode) {
            javax.swing.tree.DefaultMutableTreeNode node = 
                (javax.swing.tree.DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            
            if (userObject instanceof Project) {
                Project project = (Project) userObject;
                setText(project.getName());
                setIcon(createProjectIcon());
            } else if (userObject instanceof AnimationAction) {
                AnimationAction action = (AnimationAction) userObject;
                setText(action.getName() + " (" + action.getType() + ")");
                setIcon(createActionIcon(action.getType()));
            } else {
                setText(userObject.toString());
            }
        }
        
        return this;
    }
    
    private Icon createProjectIcon() {
        return new ColorIcon(new Color(100, 149, 237), 16, 16); // Cornflower blue
    }
    
    private Icon createActionIcon(String actionType) {
        Color color;
        switch (actionType) {
            case "Stay":
                color = new Color(34, 139, 34); // Forest green
                break;
            case "Move":
                color = new Color(255, 165, 0); // Orange
                break;
            case "Animate":
                color = new Color(220, 20, 60); // Crimson
                break;
            case "Sequence":
                color = new Color(138, 43, 226); // Blue violet
                break;
            case "Select":
                color = new Color(255, 215, 0); // Gold
                break;
            case "Embedded":
                color = new Color(169, 169, 169); // Dark gray
                break;
            default:
                color = new Color(105, 105, 105); // Dim gray
                break;
        }
        return new ColorIcon(color, 12, 12);
    }
    
    /**
     * Simple colored icon implementation
     */
    private static class ColorIcon implements Icon {
        private Color color;
        private int width;
        private int height;
        
        public ColorIcon(Color color, int width, int height) {
            this.color = color;
            this.width = width;
            this.height = height;
        }
        
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.fillOval(x, y, width, height);
            g2d.setColor(color.darker());
            g2d.drawOval(x, y, width, height);
            g2d.dispose();
        }
        
        @Override
        public int getIconWidth() {
            return width;
        }
        
        @Override
        public int getIconHeight() {
            return height;
        }
    }
}
