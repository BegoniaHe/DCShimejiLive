package com.group_finity.mascot.animationeditor;

import com.group_finity.mascot.animationeditor.ui.MainEditorWindow;
import com.group_finity.mascot.license.LicenseChecker;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import com.formdev.flatlaf.FlatDarkLaf;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * 动画编辑器主入口
 * Animation Editor Main Entry Point
 * 
 * @author DCShimeji Team
 */
public class AnimationEditor {
    private static final Logger log = Logger.getLogger(AnimationEditor.class.getName());
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 设置Look and Feel
                FlatDarkLaf.setup();
                UIManager.setLookAndFeel(new FlatDarkLaf());
                
                // 启动动画编辑器
                new MainEditorWindow().setVisible(true);
                
            } catch (Exception e) {
                log.log(Level.SEVERE, "Failed to start Animation Editor", e);
                System.exit(1);
            }
        });
    }
    
    /**
     * 从主程序启动动画编辑器
     * Launch Animation Editor from main program
     */
    public static void launch() {
        // Check special license permission first
        if (!LicenseChecker.checkSpecialFeature(true)) {
            log.log(Level.WARNING, "Animation Editor access denied - Special license required");
            return; // Permission denied, error dialog already shown by checkSpecialFeature
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                log.log(Level.INFO, "Launching Animation Editor with Special license authorization");
                MainEditorWindow editorWindow = new MainEditorWindow();
                
                // Add special license indicator to title
                editorWindow.setTitle("Shimeji Animation Editor - Developer Version");
                editorWindow.setVisible(true);
                
            } catch (Exception e) {
                log.log(Level.SEVERE, "Failed to launch Animation Editor", e);
                JOptionPane.showMessageDialog(null, 
                    "Failed to launch Animation Editor: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
