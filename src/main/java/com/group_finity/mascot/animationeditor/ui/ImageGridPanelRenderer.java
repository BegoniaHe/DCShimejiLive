package com.group_finity.mascot.animationeditor.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 图片网格面板渲染器 - 用于创建统一的图片预览面板
 * Image Grid Panel Renderer - For creating uniform image preview panels
 * 
 * @author DCShimeji Team
 */
public class ImageGridPanelRenderer {
    private static final ConcurrentHashMap<String, ImageIcon> iconCache = new ConcurrentHashMap<>();
    private static final int PREVIEW_WIDTH = 100;
    private static final int PREVIEW_HEIGHT = 80;
    private static final int PANEL_WIDTH = 200;
    private static final int PANEL_HEIGHT = 100;
    
    /**
     * 创建图片预览面板
     * Create image preview panel
     */
    public static JPanel createImagePanel(File imageFile, Runnable onClickAction, Runnable onDoubleClickAction) {
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        imagePanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Image preview (left side)
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(PREVIEW_WIDTH + 15, PANEL_HEIGHT - 10));
        
        // Load image with caching
        ImageIcon icon = getCachedIcon(imageFile);
        if (icon != null) {
            imageLabel.setIcon(icon);
        } else {
            imageLabel.setText("<html><center>无法<br/>预览</center></html>");
            imageLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
            imageLabel.setForeground(Color.GRAY);
        }
        
        // File name label (right side)
        String displayName = imageFile.getName();
        // If filename is too long, break it nicely
        if (displayName.length() > 20) {
            // Find a good break point (dot before extension)
            int dotIndex = displayName.lastIndexOf('.');
            if (dotIndex > 15) {
                displayName = displayName.substring(0, 15) + "...<br/>" + 
                            displayName.substring(dotIndex - 3);
            }
        }
        
        JLabel nameLabel = new JLabel("<html><div style='word-wrap: break-word; width: 100px;'>" + 
                                     displayName + "</div></html>");
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        nameLabel.setVerticalAlignment(SwingConstants.CENTER);
        nameLabel.setToolTipText(imageFile.getName());
        nameLabel.setForeground(new Color(64, 64, 64)); // Darker text
        nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        
        imagePanel.add(imageLabel, BorderLayout.WEST);
        imagePanel.add(nameLabel, BorderLayout.CENTER);
        
        // Mouse handling
        MouseHandler mouseHandler = new MouseHandler(onClickAction, onDoubleClickAction);
        imagePanel.addMouseListener(mouseHandler);
        imageLabel.addMouseListener(mouseHandler);
        nameLabel.addMouseListener(mouseHandler);
        
        return imagePanel;
    }
    
    /**
     * 获取缓存的图标
     */
    private static ImageIcon getCachedIcon(File imageFile) {
        String cacheKey = imageFile.getAbsolutePath() + "_" + imageFile.lastModified();
        ImageIcon icon = iconCache.get(cacheKey);
        
        if (icon == null) {
            icon = createThumbnailIcon(imageFile);
            if (icon != null) {
                iconCache.put(cacheKey, icon);
            }
        }
        
        return icon;
    }
    
    /**
     * 创建缩略图图标
     */
    private static ImageIcon createThumbnailIcon(File imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (image != null) {
                // 保持长宽比缩放
                Image scaledImage = scaleImageToFit(image, PREVIEW_WIDTH, PREVIEW_HEIGHT);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            // 图片加载失败，返回null使用默认文本
        }
        return null;
    }
    
    /**
     * 按比例缩放图片以适应指定尺寸
     */
    private static Image scaleImageToFit(BufferedImage originalImage, int maxWidth, int maxHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        double scaleX = (double) maxWidth / originalWidth;
        double scaleY = (double) maxHeight / originalHeight;
        double scale = Math.min(scaleX, scaleY);
        
        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);
        
        return originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }
    
    /**
     * 高亮选中的图片面板
     */
    public static void highlightPanel(JPanel selectedPanel, Container parentContainer) {
        // 移除所有面板的高亮
        for (Component comp : parentContainer.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(Color.WHITE);
                ((JPanel) comp).setBorder(BorderFactory.createRaisedBevelBorder());
            }
        }
        
        // 高亮选中的面板
        selectedPanel.setBackground(new Color(173, 216, 230)); // 浅蓝色
        selectedPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createLineBorder(new Color(0, 123, 255), 2) // 蓝色边框
        ));
        selectedPanel.repaint();
    }
    
    /**
     * 清除图标缓存以释放内存
     */
    public static void clearCache() {
        iconCache.clear();
    }
    
    /**
     * 鼠标事件处理器
     */
    private static class MouseHandler extends java.awt.event.MouseAdapter {
        private final Runnable onClickAction;
        private final Runnable onDoubleClickAction;
        
        public MouseHandler(Runnable onClickAction, Runnable onDoubleClickAction) {
            this.onClickAction = onClickAction;
            this.onDoubleClickAction = onDoubleClickAction;
        }
        
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            if (evt.getClickCount() == 1 && onClickAction != null) {
                onClickAction.run();
            } else if (evt.getClickCount() == 2 && onDoubleClickAction != null) {
                onDoubleClickAction.run();
            }
        }
        
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            Component source = evt.getComponent();
            if (source instanceof JPanel) {
                JPanel panel = (JPanel) source;
                if (panel.getBackground().equals(Color.WHITE)) {
                    panel.setBackground(new Color(245, 245, 245));
                }
            }
        }
        
        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            Component source = evt.getComponent();
            if (source instanceof JPanel) {
                JPanel panel = (JPanel) source;
                if (panel.getBackground().equals(new Color(245, 245, 245))) {
                    panel.setBackground(Color.WHITE);
                }
            }
        }
    }
}
