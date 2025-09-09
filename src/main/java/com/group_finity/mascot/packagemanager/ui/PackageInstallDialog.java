package com.group_finity.mascot.packagemanager.ui;

import com.group_finity.mascot.packagemanager.MascotPackageManager;
import com.group_finity.mascot.packagemanager.MascotPackage;
import com.group_finity.mascot.DPIManager;
import com.group_finity.mascot.Main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;

/**
 * 桌宠包安装界面
 * Mascot Package Installation UI
 * 
 * @author DCShimeji Team
 */
public class PackageInstallDialog extends JDialog {
    private JProgressBar progressBar;
    private JButton installButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    private JLabel packageFileLabel;
    private JTextArea packageInfoArea;
    private File selectedPackage;
    private ResourceBundle languageBundle;
    
    public PackageInstallDialog(Frame parent) {
        super(parent, "Install Mascot Package", true);
        this.languageBundle = Main.getInstance().getLanguageBundle();
        initComponents();
        applyDPIScaling();
        setLocationRelativeTo(parent);
    }
    
    /**
     * 初始化组件
     * Initialize components
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        // DPI 缩放在 applyDPIScaling() 方法中处理
        
        // 主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 包文件选择
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel(languageBundle.getString("PackageFile")), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        packageFileLabel = new JLabel(languageBundle.getString("NotSelected"));
        packageFileLabel.setBorder(BorderFactory.createEtchedBorder());
        packageFileLabel.setPreferredSize(new Dimension(400, 25));
        packageFileLabel.setOpaque(true);
        packageFileLabel.setBackground(Color.WHITE);
        mainPanel.add(packageFileLabel, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JButton browseButton = new JButton(languageBundle.getString("Browse"));
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".smp");
                }
                
                @Override
                public String getDescription() {
                    return "Shimeji Mascot Package (*.smp)";
                }
            });
            
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedPackage = fileChooser.getSelectedFile();
                packageFileLabel.setText(selectedPackage.getName());
                packageFileLabel.setToolTipText(selectedPackage.getAbsolutePath());
                installButton.setEnabled(true);
                
                // 显示包信息
                try {
                    MascotPackage packageInfo = MascotPackageManager.readPackageInfo(selectedPackage);
                    StringBuilder info = new StringBuilder();
                    info.append(languageBundle.getString("Name")).append(": ").append(packageInfo.getName()).append("\n");
                    info.append(languageBundle.getString("Version")).append(": ").append(packageInfo.getVersion()).append("\n");
                    info.append(languageBundle.getString("Author")).append(": ").append(packageInfo.getAuthor()).append("\n");
                    info.append(languageBundle.getString("Description")).append(": ").append(packageInfo.getDescription()).append("\n");
                    info.append(languageBundle.getString("Created")).append(": ").append(packageInfo.getCreateTime()).append("\n");
                    
                    packageInfoArea.setText(info.toString());
                    statusLabel.setText(languageBundle.getString("PackageSelectedClickInstall"));
                    statusLabel.setForeground(new Color(0, 120, 0));
                } catch (Exception ex) {
                    packageInfoArea.setText(languageBundle.getString("ErrorReadingPackage") + "\n\n" + ex.getMessage());
                    statusLabel.setText(languageBundle.getString("PackageFileCorrupted"));
                    statusLabel.setForeground(Color.RED);
                }
            }
        });
        mainPanel.add(browseButton, gbc);
        
        // 包信息显示区域
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE; gbc.weighty = 0;
        gbc.insets = new Insets(15, 5, 5, 5);
        mainPanel.add(new JLabel(languageBundle.getString("PackageInfo")), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.3;
        packageInfoArea = new JTextArea(4, 40);
        packageInfoArea.setEditable(false);
        packageInfoArea.setBackground(new Color(245, 245, 245));
        packageInfoArea.setText(languageBundle.getString("SelectPackageForDetails"));
        packageInfoArea.setBorder(BorderFactory.createLoweredBevelBorder());
        JScrollPane packageInfoScrollPane = new JScrollPane(packageInfoArea);
        mainPanel.add(packageInfoScrollPane, gbc);
        
        // 状态标签
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3; gbc.weightx = 1.0; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 5, 5, 5);
        statusLabel = new JLabel(languageBundle.getString("SelectPackageToInstall"));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        statusLabel.setForeground(Color.GRAY);
        mainPanel.add(statusLabel, gbc);
        
        // 进度条
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 15, 5);
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("Ready to install...");
        progressBar.setVisible(false); // 初始隐藏
        mainPanel.add(progressBar, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        installButton = new JButton(languageBundle.getString("Install"));
        installButton.setEnabled(false);
        installButton.addActionListener(new InstallAction());
        
        cancelButton = new JButton(languageBundle.getString("Cancel"));
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(installButton);
        buttonPanel.add(cancelButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 安装操作处理器
     * Install action handler
     */
    private class InstallAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedPackage == null || !selectedPackage.exists()) {
                JOptionPane.showMessageDialog(PackageInstallDialog.this,
                    languageBundle.getString("SelectValidPackageFile"),
                    languageBundle.getString("Error"),
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 禁用按钮并显示进度
            installButton.setEnabled(false);
            cancelButton.setEnabled(false);
            progressBar.setVisible(true);
            statusLabel.setText("正在准备安装... / Preparing installation...");
            statusLabel.setForeground(Color.BLUE);
            
            // 在后台线程中执行安装
            SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    // 添加小延迟让用户看到进度开始
                    Thread.sleep(200);
                    return MascotPackageManager.installPackage(selectedPackage, progressBar);
                }
                
                @Override
                protected void process(java.util.List<String> chunks) {
                    for (String message : chunks) {
                        statusLabel.setText(message);
                    }
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            statusLabel.setText(languageBundle.getString("InstallationSuccessful"));
                            statusLabel.setForeground(new Color(0, 150, 0));
                            progressBar.setString("Installation Complete!");
                            
                            // 短暂显示成功状态后关闭对话框
                            Timer timer = new Timer(1500, evt -> dispose());
                            timer.setRepeats(false);
                            timer.start();
                        } else {
                            statusLabel.setText(languageBundle.getString("InstallationFailed"));
                            statusLabel.setForeground(Color.RED);
                            progressBar.setString("Installation Failed");
                        }
                    } catch (Exception ex) {
                        statusLabel.setText(languageBundle.getString("InstallationError"));
                        statusLabel.setForeground(Color.RED);
                        progressBar.setString("Error occurred");
                        
                        JOptionPane.showMessageDialog(PackageInstallDialog.this,
                            languageBundle.getString("ErrorDuringInstallation") + ": " + ex.getMessage(),
                            languageBundle.getString("InstallationError"),
                            JOptionPane.ERROR_MESSAGE);
                    } finally {
                        // 重新启用按钮
                        installButton.setEnabled(true);
                        cancelButton.setEnabled(true);
                    }
                }
            };
            
            worker.execute();
        }
    }
    
    /**
     * 应用DPI缩放
     * Apply DPI scaling
     */
    private void applyDPIScaling() {
        // 使用 DPI 缩放设置窗口大小
        int scaledWidth = DPIManager.scaleWidth(650);
        int scaledHeight = DPIManager.scaleHeight(480);
        setSize(scaledWidth, scaledHeight);
    }
    
    /**
     * 显示安装对话框
     * Show install dialog
     * 
     * @param parent 父窗口
     */
    public static void showInstallDialog(Frame parent) {
        PackageInstallDialog dialog = new PackageInstallDialog(parent);
        dialog.setVisible(true);
    }
}
