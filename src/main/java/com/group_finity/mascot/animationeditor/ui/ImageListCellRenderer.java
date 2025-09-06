package com.group_finity.mascot.animationeditor.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * 图片列表单元格渲染器
 * Image List Cell Renderer
 * 
 * @author DCShimeji Team
 */
public class ImageListCellRenderer extends DefaultListCellRenderer {
    
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        if (value instanceof File) {
            File imageFile = (File) value;
            setText(imageFile.getName());
            
            // Try to create a thumbnail icon
            try {
                ImageIcon originalIcon = new ImageIcon(imageFile.getAbsolutePath());
                if (originalIcon.getIconWidth() > 0) {
                    // Scale the image to a thumbnail size
                    Image scaledImage = originalIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                    setIcon(new ImageIcon(scaledImage));
                } else {
                    setIcon(createFileIcon());
                }
            } catch (Exception e) {
                // If image loading fails, use a generic file icon
                setIcon(createFileIcon());
            }
        }
        
        return this;
    }
    
    private Icon createFileIcon() {
        return new FileIcon(16, 16);
    }
    
    /**
     * Simple file icon implementation
     */
    private static class FileIcon implements Icon {
        private int width;
        private int height;
        
        public FileIcon(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw a simple file icon
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(x + 2, y + 2, width - 6, height - 4);
            
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRect(x + 2, y + 2, width - 6, height - 4);
            
            // Draw fold corner
            g2d.setColor(Color.WHITE);
            g2d.fillPolygon(new int[]{x + width - 6, x + width - 2, x + width - 2}, 
                           new int[]{y + 2, y + 2, y + 6}, 3);
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawLine(x + width - 6, y + 2, x + width - 2, y + 6);
            g2d.drawLine(x + width - 6, y + 2, x + width - 6, y + 6);
            g2d.drawLine(x + width - 6, y + 6, x + width - 2, y + 6);
            
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
