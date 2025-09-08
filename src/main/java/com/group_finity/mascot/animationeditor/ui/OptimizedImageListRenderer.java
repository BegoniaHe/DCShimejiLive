package com.group_finity.mascot.animationeditor.ui;

import com.group_finity.mascot.animationeditor.util.ProjectScanner;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 优化的图片列表渲染器 - 提高性能
 * Optimized Image List Renderer - Better Performance
 * 
 * @author DCShimeji Team
 */
public class OptimizedImageListRenderer extends DefaultListCellRenderer {
    private static final ConcurrentHashMap<String, ImageIcon> iconCache = new ConcurrentHashMap<>();
    private static final int ICON_SIZE = 32;
    private static final Icon DEFAULT_FILE_ICON = createDefaultFileIcon();
    
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        if (value instanceof File) {
            File imageFile = (File) value;
            setText(imageFile.getName());
            
            // Use cached icon or create new one
            String cacheKey = imageFile.getAbsolutePath() + "_" + imageFile.lastModified();
            ImageIcon icon = iconCache.get(cacheKey);
            
            if (icon == null) {
                icon = createThumbnailIcon(imageFile);
                if (icon != null) {
                    iconCache.put(cacheKey, icon);
                } else {
                    setIcon(DEFAULT_FILE_ICON);
                    return this;
                }
            }
            
            setIcon(icon);
        }
        
        return this;
    }
    
    private ImageIcon createThumbnailIcon(File imageFile) {
        try {
            // Only create thumbnails for supported image files
            if (!ProjectScanner.isImageFile(imageFile)) {
                return null;
            }
            
            ImageIcon originalIcon = new ImageIcon(imageFile.getAbsolutePath());
            if (originalIcon.getIconWidth() > 0 && originalIcon.getIconHeight() > 0) {
                // Scale the image to thumbnail size
                Image scaledImage = originalIcon.getImage().getScaledInstance(
                    ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            // If image loading fails, return null to use default icon
        }
        return null;
    }
    
    private static Icon createDefaultFileIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw a simple file icon
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillRect(x + 2, y + 2, getIconWidth() - 6, getIconHeight() - 4);
                
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRect(x + 2, y + 2, getIconWidth() - 6, getIconHeight() - 4);
                
                // Draw fold corner
                int cornerSize = 6;
                g2d.setColor(Color.WHITE);
                g2d.fillPolygon(
                    new int[]{x + getIconWidth() - cornerSize - 2, x + getIconWidth() - 2, x + getIconWidth() - 2}, 
                    new int[]{y + 2, y + 2, y + cornerSize + 2}, 3);
                
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawLine(x + getIconWidth() - cornerSize - 2, y + 2, x + getIconWidth() - 2, y + cornerSize + 2);
                g2d.drawLine(x + getIconWidth() - cornerSize - 2, y + 2, x + getIconWidth() - cornerSize - 2, y + cornerSize + 2);
                g2d.drawLine(x + getIconWidth() - cornerSize - 2, y + cornerSize + 2, x + getIconWidth() - 2, y + cornerSize + 2);
                
                g2d.dispose();
            }
            
            @Override
            public int getIconWidth() { return ICON_SIZE; }
            
            @Override
            public int getIconHeight() { return ICON_SIZE; }
        };
    }
    
    // Clear cache when needed to prevent memory leaks
    public static void clearCache() {
        iconCache.clear();
    }
}
