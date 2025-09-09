package com.group_finity.mascot.packagemanager.ui;

import com.group_finity.mascot.DPIManager;
import com.group_finity.mascot.Main;
import com.group_finity.mascot.packagemanager.MascotPackageManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 桌宠包创建工具界面 - 支持本地化文件
 * Mascot Package Creation Tool UI with Localization Support
 * 
 * @author DCShimeji Team
 */
public class PackageCreatorDialog extends JDialog {
    private JTextField nameField;
    private JTextField authorField;
    private JTextArea descriptionArea;
    private JTextField versionField;
    private JLabel mascotFolderLabel;
    private JLabel languageFilesLabel;
    private JButton createButton;
    private JButton cancelButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private File selectedMascotFolder;
    private ResourceBundle languageBundle;
    
    public PackageCreatorDialog(Frame parent) {
        super(parent, "Package Creator", true);
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
        
        // 使用 DPI 缩放设置窗口大小
        int scaledWidth = DPIManager.scaleWidth(650);
        int scaledHeight = DPIManager.scaleHeight(480);
        setSize(scaledWidth, scaledHeight);
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 桌宠文件夹选择
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel(languageBundle.getString("MascotFolder")), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        mascotFolderLabel = new JLabel(languageBundle.getString("NotSelected"));
        mascotFolderLabel.setBorder(BorderFactory.createEtchedBorder());
        mascotFolderLabel.setPreferredSize(new Dimension(300, 25));
        mascotFolderLabel.setOpaque(true);
        mascotFolderLabel.setBackground(Color.WHITE);
        mainPanel.add(mascotFolderLabel, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JButton browseFolderButton = new JButton(languageBundle.getString("Browse"));
        browseFolderButton.addActionListener(this::browseMascotFolder);
        mainPanel.add(browseFolderButton, gbc);
        
        // 包信息输入
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        mainPanel.add(new JLabel(languageBundle.getString("PackageName")), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        nameField = new JTextField();
        mainPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0;
        mainPanel.add(new JLabel(languageBundle.getString("Author")), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        authorField = new JTextField();
        mainPanel.add(authorField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.weightx = 0;
        mainPanel.add(new JLabel(languageBundle.getString("Version")), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        versionField = new JTextField("1.0");
        mainPanel.add(versionField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1; gbc.weightx = 0;
        mainPanel.add(new JLabel(languageBundle.getString("Description")), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.3;
        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        mainPanel.add(descScrollPane, gbc);
        
        // 语言文件信息显示
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1; gbc.weightx = 0; gbc.weighty = 0;
        mainPanel.add(new JLabel(languageBundle.getString("Localization")), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        languageFilesLabel = new JLabel(languageBundle.getString("SelectMascotFolderFirst"));
        languageFilesLabel.setBorder(BorderFactory.createEtchedBorder());
        languageFilesLabel.setOpaque(true);
        languageFilesLabel.setBackground(Color.WHITE);
        mainPanel.add(languageFilesLabel, gbc);
        
        // 添加状态标签和进度条
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        statusLabel = new JLabel(languageBundle.getString("Ready"));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        statusLabel.setForeground(Color.GRAY);
        mainPanel.add(statusLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("Ready to create package...");
        progressBar.setVisible(false); // 初始隐藏
        mainPanel.add(progressBar, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        createButton = new JButton(languageBundle.getString("CreatePackage"));
        createButton.setEnabled(false);
        createButton.addActionListener(this::createPackage);
        
        cancelButton = new JButton(languageBundle.getString("Cancel"));
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 浏览桌宠文件夹
     * Browse mascot folder
     */
    private void browseMascotFolder(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser("img");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("选择桌宠文件夹 / Select Mascot Folder");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedMascotFolder = fileChooser.getSelectedFile();
            mascotFolderLabel.setText(selectedMascotFolder.getName());
            
            // 自动填充包名称
            if (nameField.getText().isEmpty()) {
                nameField.setText(selectedMascotFolder.getName());
            }
            
            // 扫描和显示本地化文件
            scanAndDisplayLanguageFiles();
            
            createButton.setEnabled(true);
        }
    }
    
    /**
     * 扫描并显示语言文件
     * Scan and display language files
     */
    private void scanAndDisplayLanguageFiles() {
        if (selectedMascotFolder == null) return;
        
        File confFolder = new File(selectedMascotFolder, "conf");
        List<String> languageFiles = new ArrayList<>();
        List<String> supportedLanguages = new ArrayList<>();
        
        if (confFolder.exists() && confFolder.isDirectory()) {
            File[] files = confFolder.listFiles((dir, name) -> name.endsWith(".properties"));
            if (files != null) {
                for (File file : files) {
                    languageFiles.add(file.getName());
                    
                    if (file.getName().startsWith("language_")) {
                        String langCode = file.getName().substring(9, file.getName().length() - 11);
                        supportedLanguages.add(langCode);
                    } else if (file.getName().equals("language.properties")) {
                        supportedLanguages.add("default");
                    }
                }
            }
        }
        
        if (languageFiles.isEmpty()) {
            languageFilesLabel.setText("未找到本地化文件 / No localization files found");
            languageFilesLabel.setForeground(Color.ORANGE);
        } else {
            languageFilesLabel.setText(String.format(
                "找到 %d 个语言文件，支持: %s / Found %d language files, supporting: %s",
                languageFiles.size(),
                String.join(", ", supportedLanguages),
                languageFiles.size(),
                String.join(", ", supportedLanguages)
            ));
            languageFilesLabel.setForeground(Color.BLUE);
        }
    }
    
    /**
     * 创建包
     * Create package
     */
    private void createPackage(ActionEvent e) {
        if (selectedMascotFolder == null || nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "请填写必需的信息 / Please fill in required information",
                "信息不完整 / Incomplete Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
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
        
        String defaultFileName = nameField.getText().trim().replaceAll("[^a-zA-Z0-9_-]", "") + ".smp";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            final File outputFile = selectedFile.getName().toLowerCase().endsWith(".smp") ? 
                selectedFile : new File(selectedFile.getParentFile(), selectedFile.getName() + ".smp");
            
            // 禁用按钮并显示进度
            createButton.setEnabled(false);
            cancelButton.setEnabled(false);
            progressBar.setVisible(true);
            statusLabel.setText("正在创建包... / Creating package...");
            statusLabel.setForeground(Color.BLUE);
            progressBar.setValue(0);
            progressBar.setString("Initializing...");
            
            SwingWorker<Boolean, Integer> worker = new SwingWorker<Boolean, Integer>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    // 模拟进度更新
                    publish(10);
                    Thread.sleep(100); // 短暂延迟让用户看到进度开始
                    
                    publish(30);
                    Thread.sleep(100);
                    
                    boolean result = MascotPackageManager.createPackage(
                        selectedMascotFolder,
                        outputFile,
                        nameField.getText().trim(),
                        authorField.getText().trim(),
                        descriptionArea.getText().trim()
                    );
                    
                    publish(90);
                    Thread.sleep(100);
                    
                    publish(100);
                    return result;
                }
                
                @Override
                protected void process(java.util.List<Integer> chunks) {
                    for (Integer progress : chunks) {
                        progressBar.setValue(progress);
                        if (progress < 30) {
                            progressBar.setString("Scanning files...");
                        } else if (progress < 60) {
                            progressBar.setString("Creating package...");
                        } else if (progress < 90) {
                            progressBar.setString("Writing data...");
                        } else {
                            progressBar.setString("Finalizing...");
                        }
                    }
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            statusLabel.setText("创建成功 / Creation Successful");
                            statusLabel.setForeground(new Color(0, 150, 0));
                            progressBar.setString("Package Created!");
                            
                            JOptionPane.showMessageDialog(PackageCreatorDialog.this,
                                "桌宠包创建成功！\nMascot package created successfully!\n\n" +
                                "文件位置 / File location: " + outputFile.getAbsolutePath(),
                                "创建成功 / Creation Success",
                                JOptionPane.INFORMATION_MESSAGE);
                            
                            // 短暂显示成功状态后关闭对话框
                            Timer timer = new Timer(1000, evt -> dispose());
                            timer.setRepeats(false);
                            timer.start();
                        } else {
                            statusLabel.setText("创建失败 / Creation Failed");
                            statusLabel.setForeground(Color.RED);
                            progressBar.setString("Creation Failed");
                            
                            JOptionPane.showMessageDialog(PackageCreatorDialog.this,
                                "创建桌宠包失败 / Failed to create mascot package",
                                "创建错误 / Creation Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        statusLabel.setText("创建出错 / Creation Error");
                        statusLabel.setForeground(Color.RED);
                        progressBar.setString("Error occurred");
                        
                        JOptionPane.showMessageDialog(PackageCreatorDialog.this,
                            "创建过程中发生错误: " + ex.getMessage() + 
                            "\nError during creation: " + ex.getMessage(),
                            "创建错误 / Creation Error",
                            JOptionPane.ERROR_MESSAGE);
                    } finally {
                        // 重新启用按钮
                        createButton.setEnabled(true);
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
        // DPI缩放已在initComponents中处理
        // DPI scaling is already handled in initComponents
    }
    
    /**
     * 显示包创建对话框
     * Show package creator dialog
     * 
     * @param parent 父窗口
     */
    public static void showCreatorDialog(Frame parent) {
        PackageCreatorDialog dialog = new PackageCreatorDialog(parent);
        dialog.setVisible(true);
    }
}
