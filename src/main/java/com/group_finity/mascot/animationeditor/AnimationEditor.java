package com.group_finity.mascot.animationeditor;

import com.group_finity.mascot.animationeditor.ui.MainEditorWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
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
        SwingUtilities.invokeLater(() -> {
            try {
                new MainEditorWindow().setVisible(true);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Failed to launch Animation Editor", e);
            }
        });
    }
}
