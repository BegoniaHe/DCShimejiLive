package com.group_finity.mascot.packagemanager;

import com.group_finity.mascot.packagemanager.ui.PackageInstallDialog;
import com.group_finity.mascot.packagemanager.ui.PackageCreatorDialog;

import javax.swing.*;
import java.awt.*;

/**
 * 桌宠包管理系统测试
 * Mascot Package Management System Test
 * 
 * @author DCShimeji Team
 */
public class PackageManagerTest {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 设置Look and Feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            createTestUI();
        });
    }
    
    /**
     * 创建测试UI
     * Create test UI
     */
    private static void createTestUI() {
        JFrame frame = new JFrame("Mascot Package Manager Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton installButton = new JButton("Test Install Package Dialog");
        installButton.addActionListener(e -> {
            PackageInstallDialog.showInstallDialog(frame);
        });
        
        JButton createButton = new JButton("Test Create Package Dialog");
        createButton.addActionListener(e -> {
            PackageCreatorDialog.showCreatorDialog(frame);
        });
        
        JButton testMergeButton = new JButton("Test Localization Merge");
        testMergeButton.addActionListener(e -> {
            testLocalizationMerge();
        });
        
        panel.add(installButton);
        panel.add(createButton);
        panel.add(testMergeButton);
        
        frame.add(panel);
        frame.setVisible(true);
    }
    
    /**
     * 测试本地化合并功能
     * Test localization merge functionality
     */
    private static void testLocalizationMerge() {
        // 创建测试数据
        java.util.Map<String, java.util.Properties> testLocalization = new java.util.HashMap<>();
        
        java.util.Properties testProps = new java.util.Properties();
        testProps.setProperty("TestAction1", "Test Action 1");
        testProps.setProperty("TestAction2", "Test Action 2");
        testProps.setProperty("CommonAction", "Common Action from Test Package");
        
        testLocalization.put("language.properties", testProps);
        
        // 测试冲突检测
        LocalizationConflictReport conflictReport = 
            LocalizationMergeManager.checkConflicts("TestMascot", testLocalization);
        
        if (conflictReport.hasConflicts()) {
            JTextArea textArea = new JTextArea(conflictReport.getFormattedReport());
            textArea.setEditable(false);
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            
            JOptionPane.showMessageDialog(null, scrollPane, 
                "Localization Conflicts Test", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // 测试合并功能
            boolean mergeResult = LocalizationMergeManager.mergeLocalizationFiles("TestMascot", testLocalization);
            
            JOptionPane.showMessageDialog(null, 
                "Localization merge test completed.\n" +
                "Result: " + (mergeResult ? "Success" : "Failed") + "\n" +
                "Check conf/language.properties for merged entries.",
                "Localization Merge Test", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
