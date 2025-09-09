package com.group_finity.mascot.packagemanager;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import java.time.format.DateTimeFormatter;

/**
 * 桌宠包管理器 - 支持本地化文件
 * Mascot Package Manager with Localization Support
 * 
 * @author DCShimeji Team
 */
public class MascotPackageManager {
    private static final Logger log = Logger.getLogger(MascotPackageManager.class.getName());
    private static final String MANIFEST_FILE = "manifest.json";
    private static final String PREVIEW_FILE = "preview.png";
    private static final String DATA_FOLDER = "mascot_data";
    private static final String CONF_FOLDER = "conf";
    
    /**
     * 创建桌宠包
     * Create mascot package from existing mascot folder
     * 
     * @param mascotFolder 桌宠文件夹
     * @param outputFile 输出文件
     * @param name 包名
     * @param author 作者
     * @param description 描述
     * @return true if successful, false otherwise
     */
    public static boolean createPackage(File mascotFolder, File outputFile, 
                                      String name, String author, String description) {
        try {
            // 创建包元数据
            MascotPackage packageData = new MascotPackage();
            packageData.setName(name);
            packageData.setAuthor(author);
            packageData.setDescription(description);
            packageData.setVersion("1.0");
            packageData.setCreateTime(java.time.LocalDateTime.now());
            
            // 扫描文件
            scanMascotFolder(mascotFolder, packageData);
            
            // 创建ZIP包
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFile))) {
                // 写入manifest
                writeManifest(zos, packageData);
                
                // 写入预览图片（如果存在）
                writePreviewImage(zos, mascotFolder, packageData);
                
                // 写入桌宠数据
                writeMascotData(zos, mascotFolder);
                
                log.info("Successfully created package: " + outputFile.getName());
                return true;
            }
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to create package", e);
            return false;
        }
    }
    
    /**
     * 安装桌宠包，使用新的本地化合并机制
     * Install mascot package using new localization merge mechanism
     * 
     * @param packageFile 包文件
     * @param progressBar 进度条
     * @return true if successful, false otherwise
     */
    public static boolean installPackage(File packageFile, JProgressBar progressBar) {
        try {
            // 验证包文件
            if (!packageFile.exists() || !packageFile.getName().endsWith(".smp")) {
                throw new IllegalArgumentException("Invalid package file");
            }
            
            progressBar.setValue(10);
            progressBar.setString("Reading package...");
            
            // 读取包信息
            MascotPackage packageData;
            try (ZipFile zipFile = new ZipFile(packageFile)) {
                packageData = readManifest(zipFile);
                if (packageData == null || !packageData.validatePackage()) {
                    throw new IllegalArgumentException("Invalid package manifest");
                }
                
                progressBar.setValue(20);
                progressBar.setString("Reading localization data...");
                
                // 读取本地化数据
                Map<String, Properties> localizationData = readLocalizationData(zipFile);
                
                progressBar.setValue(30);
                progressBar.setString("Checking conflicts...");
                
                // 检查本地化冲突
                if (!localizationData.isEmpty()) {
                    LocalizationConflictReport conflictReport = 
                        LocalizationMergeManager.checkConflicts(packageData.getName(), localizationData);
                    
                    if (conflictReport.hasConflicts()) {
                        // 显示冲突对话框
                        int choice = showConflictDialog(conflictReport);
                        if (choice != JOptionPane.YES_OPTION) {
                            return false;
                        }
                    }
                }
                
                progressBar.setValue(40);
                progressBar.setString("Preparing installation...");
                
                // 检查现有安装
                File targetFolder = new File("img", packageData.getName());
                if (targetFolder.exists()) {
                    int result = JOptionPane.showConfirmDialog(null,
                        String.format(
                            "桌宠 '%s' 已存在。覆盖安装将会：\n" +
                            "1. 替换所有桌宠文件\n" +
                            "2. 合并本地化文件（解决冲突）\n" +
                            "3. 保留现有本地化备份\n\n" +
                            "是否继续？\n\n" +
                            "Mascot '%s' already exists. Overwrite will:\n" +
                            "1. Replace all mascot files\n" +
                            "2. Merge localization files (resolve conflicts)\n" +
                            "3. Keep existing localization backup\n\n" +
                            "Continue?",
                            packageData.getName(), packageData.getName()
                        ),
                        "确认覆盖安装 / Confirm Overwrite Installation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                    
                    if (result != JOptionPane.YES_OPTION) {
                        return false;
                    }
                    
                    // 先移除现有的本地化条目
                    LocalizationMergeManager.removeLocalizationEntries(packageData.getName());
                    
                    // 删除现有桌宠文件夹
                    deleteDirectory(targetFolder);
                }
                
                progressBar.setValue(50);
                progressBar.setString("Extracting mascot files...");
                
                // 创建目标文件夹
                targetFolder.mkdirs();
                
                // 解压桌宠数据（不包括本地化文件）
                extractMascotData(zipFile, targetFolder, progressBar);
                
                progressBar.setValue(80);
                progressBar.setString("Merging localization files...");
                
                // 合并本地化文件
                if (!localizationData.isEmpty()) {
                    boolean mergeSuccess = LocalizationMergeManager.mergeLocalizationFiles(
                        packageData.getName(), localizationData);
                    
                    if (!mergeSuccess) {
                        log.warning("Failed to merge localization files, but installation continues");
                    }
                }
                
                progressBar.setValue(90);
                progressBar.setString("Finalizing installation...");
                
                // 重新加载配置（如果Main实例存在）
                try {
                    // Note: This would need to be implemented in Main class
                    log.info("Configuration reload requested for: " + packageData.getName());
                } catch (Exception e) {
                    log.warning("Failed to reload configuration: " + e.getMessage());
                }
                
                progressBar.setValue(100);
                progressBar.setString("Installation complete!");
                
                // 显示安装摘要
                showInstallationSummary(packageData, localizationData);
                
                log.info("Successfully installed package: " + packageData.getName());
                return true;
            }
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to install package", e);
            JOptionPane.showMessageDialog(null,
                "安装失败: " + e.getMessage() + "\nInstallation failed: " + e.getMessage(),
                "安装错误 / Installation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * 卸载桌宠包
     * Uninstall mascot package
     * 
     * @param mascotName 桌宠名称
     * @return true if successful, false otherwise
     */
    public static boolean uninstallMascot(String mascotName) {
        try {
            File mascotFolder = new File("img", mascotName);
            if (!mascotFolder.exists()) {
                JOptionPane.showMessageDialog(null,
                    "桌宠不存在 / Mascot does not exist: " + mascotName,
                    "错误 / Error",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            int confirm = JOptionPane.showConfirmDialog(null,
                String.format(
                    "确定要卸载桌宠 '%s' 吗？\n" +
                    "这将删除所有相关文件并移除对应的本地化条目。\n\n" +
                    "Are you sure you want to uninstall mascot '%s'?\n" +
                    "This will delete all related files and remove corresponding localization entries.",
                    mascotName, mascotName
                ),
                "确认卸载 / Confirm Uninstall",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm != JOptionPane.YES_OPTION) {
                return false;
            }
            
            // 移除本地化条目
            LocalizationMergeManager.removeLocalizationEntries(mascotName);
            
            // 删除桌宠文件夹
            boolean deleted = deleteDirectory(mascotFolder);
            
            if (deleted) {
                JOptionPane.showMessageDialog(null,
                    "桌宠卸载成功 / Mascot uninstalled successfully: " + mascotName,
                    "卸载成功 / Uninstall Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                
                log.info("Successfully uninstalled mascot: " + mascotName);
                return true;
            } else {
                JOptionPane.showMessageDialog(null,
                    "卸载失败，某些文件可能正在使用中 / Uninstall failed, some files may be in use",
                    "卸载错误 / Uninstall Error",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to uninstall mascot: " + mascotName, e);
            return false;
        }
    }
    
    /**
     * 扫描桌宠文件夹，包含本地化文件
     * Scan mascot folder including localization files
     */
    private static void scanMascotFolder(File folder, MascotPackage packageData) {
        List<String> imageFiles = new ArrayList<>();
        List<String> soundFiles = new ArrayList<>();
        List<String> languageFiles = new ArrayList<>();
        Set<String> supportedLanguages = new HashSet<>();
        
        scanFolderRecursively(folder, "", imageFiles, soundFiles, languageFiles, supportedLanguages);
        
        packageData.setImageFiles(imageFiles);
        packageData.setSoundFiles(soundFiles);
        packageData.setLanguageFiles(languageFiles);
        packageData.setSupportedLanguages(new ArrayList<>(supportedLanguages));
        
        log.info("Scanned mascot folder: " + imageFiles.size() + " images, " + 
                soundFiles.size() + " sounds, " + languageFiles.size() + " language files");
    }
    
    /**
     * 递归扫描文件夹
     * Recursively scan folder
     */
    private static void scanFolderRecursively(File folder, String relativePath,
                                            List<String> imageFiles,
                                            List<String> soundFiles,
                                            List<String> languageFiles,
                                            Set<String> supportedLanguages) {
        File[] files = folder.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            String currentPath = relativePath.isEmpty() ? file.getName() : 
                                relativePath + "/" + file.getName();
            
            if (file.isDirectory()) {
                scanFolderRecursively(file, currentPath, imageFiles, soundFiles, 
                                    languageFiles, supportedLanguages);
            } else {
                String fileName = file.getName().toLowerCase();
                
                // 图片文件
                if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || 
                    fileName.endsWith(".jpeg") || fileName.endsWith(".gif")) {
                    imageFiles.add(currentPath);
                }
                // 音频文件
                else if (fileName.endsWith(".wav") || fileName.endsWith(".mp3") || 
                        fileName.endsWith(".ogg")) {
                    soundFiles.add(currentPath);
                }
                // 本地化文件
                else if (fileName.endsWith(".properties") && currentPath.startsWith(CONF_FOLDER + "/")) {
                    languageFiles.add(currentPath);
                    
                    // 提取语言代码
                    if (fileName.startsWith("language_") && fileName.length() > 19) {
                        String langCode = fileName.substring(9, fileName.length() - 11);
                        supportedLanguages.add(langCode);
                    } else if (fileName.equals("language.properties")) {
                        supportedLanguages.add("default");
                    }
                }
            }
        }
    }
    
    /**
     * 写入manifest文件
     * Write manifest file
     */
    private static void writeManifest(ZipOutputStream zos, MascotPackage packageData) 
            throws IOException {
        String manifestJson = packageToJson(packageData);
        
        ZipEntry entry = new ZipEntry(MANIFEST_FILE);
        zos.putNextEntry(entry);
        zos.write(manifestJson.getBytes("UTF-8"));
        zos.closeEntry();
    }
    
    /**
     * 读取manifest文件
     * Read manifest file
     */
    private static MascotPackage readManifest(ZipFile zipFile) throws IOException {
        ZipEntry manifestEntry = zipFile.getEntry(MANIFEST_FILE);
        if (manifestEntry == null) {
            return null;
        }
        
        try (InputStream is = zipFile.getInputStream(manifestEntry);
             InputStreamReader reader = new InputStreamReader(is, "UTF-8")) {
            
            StringBuilder jsonBuilder = new StringBuilder();
            char[] buffer = new char[1024];
            int length;
            while ((length = reader.read(buffer)) > 0) {
                jsonBuilder.append(buffer, 0, length);
            }
            
            return jsonToPackage(jsonBuilder.toString());
        }
    }
    
    /**
     * 写入预览图片
     * Write preview image
     */
    private static void writePreviewImage(ZipOutputStream zos, File mascotFolder, MascotPackage packageData) 
            throws IOException {
        // 查找预览图片
        File previewFile = findPreviewImage(mascotFolder);
        if (previewFile != null && previewFile.exists()) {
            ZipEntry entry = new ZipEntry(PREVIEW_FILE);
            zos.putNextEntry(entry);
            
            try (FileInputStream fis = new FileInputStream(previewFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
            }
            
            zos.closeEntry();
            packageData.setPreviewImage(PREVIEW_FILE);
        }
    }
    
    /**
     * 查找预览图片
     * Find preview image
     */
    private static File findPreviewImage(File mascotFolder) {
        String[] previewNames = {"Preview.png", "preview.png", "Stand.png", "stand.png"};
        
        for (String name : previewNames) {
            File file = new File(mascotFolder, name);
            if (file.exists()) {
                return file;
            }
        }
        
        // 如果没找到预览图片，查找第一个PNG文件
        File[] pngFiles = mascotFolder.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".png"));
        
        if (pngFiles != null && pngFiles.length > 0) {
            return pngFiles[0];
        }
        
        return null;
    }
    
    /**
     * 写入桌宠数据，包含本地化文件
     * Write mascot data including localization files
     */
    private static void writeMascotData(ZipOutputStream zos, File mascotFolder) throws IOException {
        writeFolderToZip(zos, mascotFolder, DATA_FOLDER + "/");
    }
    
    /**
     * 递归写入文件夹到ZIP
     * Recursively write folder to ZIP
     */
    private static void writeFolderToZip(ZipOutputStream zos, File folder, String basePath) 
            throws IOException {
        File[] files = folder.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            String entryName = basePath + file.getName();
            
            if (file.isDirectory()) {
                // 创建目录条目
                ZipEntry dirEntry = new ZipEntry(entryName + "/");
                zos.putNextEntry(dirEntry);
                zos.closeEntry();
                
                // 递归处理子目录
                writeFolderToZip(zos, file, entryName + "/");
            } else {
                // 写入文件
                ZipEntry fileEntry = new ZipEntry(entryName);
                zos.putNextEntry(fileEntry);
                
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                }
                
                zos.closeEntry();
            }
        }
    }
    
    /**
     * 读取包中的本地化数据
     * Read localization data from package
     */
    private static Map<String, Properties> readLocalizationData(ZipFile zipFile) throws IOException {
        Map<String, Properties> localizationData = new HashMap<>();
        
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            
            if (entry.getName().startsWith(DATA_FOLDER + "/" + CONF_FOLDER + "/") && 
                entry.getName().endsWith(".properties")) {
                
                String fullPath = entry.getName();
                String fileName = fullPath.substring((DATA_FOLDER + "/" + CONF_FOLDER + "/").length());
                
                Properties props = new Properties();
                try (InputStream is = zipFile.getInputStream(entry);
                     InputStreamReader reader = new InputStreamReader(is, "UTF-8")) {
                    props.load(reader);
                }
                
                localizationData.put(fileName, props);
            }
        }
        
        return localizationData;
    }
    
    /**
     * 解压桌宠数据
     * Extract mascot data
     */
    private static void extractMascotData(ZipFile zipFile, File targetFolder, 
                                        JProgressBar progressBar) throws IOException {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        List<ZipEntry> dataEntries = new ArrayList<>();
        
        // 收集所有数据条目
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().startsWith(DATA_FOLDER + "/") && !entry.isDirectory()) {
                dataEntries.add(entry);
            }
        }
        
        int totalEntries = dataEntries.size();
        int processedEntries = 0;
        
        // 解压文件
        for (ZipEntry entry : dataEntries) {
            String relativePath = entry.getName().substring((DATA_FOLDER + "/").length());
            File outputFile = new File(targetFolder, relativePath);
            
            // 确保父目录存在
            outputFile.getParentFile().mkdirs();
            
            // 写入文件
            try (InputStream is = zipFile.getInputStream(entry);
                 FileOutputStream fos = new FileOutputStream(outputFile)) {
                
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            }
            
            processedEntries++;
            int progress = 50 + (processedEntries * 30 / totalEntries); // 50-80%的进度
            progressBar.setValue(progress);
            progressBar.setString("Extracting: " + relativePath);
        }
    }
    
    /**
     * 显示冲突对话框
     * Show conflict dialog
     */
    private static int showConflictDialog(LocalizationConflictReport conflictReport) {
        javax.swing.JTextArea textArea = new javax.swing.JTextArea(conflictReport.getFormattedReport());
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 12));
        
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(600, 400));
        
        String message = "检测到本地化文件冲突。系统将使用带前缀的键名解决冲突，\n" +
                        "原有的本地化不会丢失。\n\n" +
                        "Localization conflicts detected. System will resolve conflicts\n" +
                        "using prefixed keys, existing localizations won't be lost.\n\n" +
                        "是否继续安装？ / Continue installation?";
        
        Object[] components = {message, scrollPane};
        
        return JOptionPane.showConfirmDialog(null, components,
            "本地化冲突 / Localization Conflicts",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * 显示安装摘要
     * Show installation summary
     */
    private static void showInstallationSummary(MascotPackage packageData, 
                                              Map<String, Properties> localizationData) {
        StringBuilder summary = new StringBuilder();
        summary.append("安装摘要 / Installation Summary:\n\n");
        summary.append("桌宠名称 / Mascot: ").append(packageData.getName()).append("\n");
        summary.append("版本 / Version: ").append(packageData.getVersion()).append("\n");
        summary.append("作者 / Author: ").append(packageData.getAuthor()).append("\n\n");
        
        if (!localizationData.isEmpty()) {
            summary.append("本地化支持 / Localization Support:\n");
            for (String langFile : localizationData.keySet()) {
                Properties props = localizationData.get(langFile);
                summary.append("  ").append(langFile).append(": ")
                       .append(props.size()).append(" entries\n");
            }
        } else {
            summary.append("本地化支持 / Localization Support: 无 / None\n");
        }
        
        JOptionPane.showMessageDialog(null, summary.toString(),
            "安装成功 / Installation Successful",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 删除目录及其内容
     * Delete directory and its contents
     */
    private static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            return directory.delete();
        }
        return false;
    }
    
    /**
     * 将包数据转换为JSON字符串
     * Convert package data to JSON string
     */
    private static String packageToJson(MascotPackage packageData) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"name\": \"").append(escapeJson(packageData.getName())).append("\",\n");
        json.append("  \"version\": \"").append(escapeJson(packageData.getVersion())).append("\",\n");
        json.append("  \"author\": \"").append(escapeJson(packageData.getAuthor())).append("\",\n");
        json.append("  \"description\": \"").append(escapeJson(packageData.getDescription())).append("\",\n");
        json.append("  \"createTime\": \"").append(packageData.getCreateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\",\n");
        json.append("  \"previewImage\": \"").append(escapeJson(packageData.getPreviewImage())).append("\",\n");
        json.append("  \"imageFiles\": ").append(listToJson(packageData.getImageFiles())).append(",\n");
        json.append("  \"soundFiles\": ").append(listToJson(packageData.getSoundFiles())).append(",\n");
        json.append("  \"languageFiles\": ").append(listToJson(packageData.getLanguageFiles())).append(",\n");
        json.append("  \"supportedLanguages\": ").append(listToJson(packageData.getSupportedLanguages())).append(",\n");
        json.append("  \"signed\": ").append(packageData.isSigned()).append("\n");
        json.append("}");
        return json.toString();
    }
    
    /**
     * 从JSON字符串转换为包数据
     * Convert JSON string to package data
     */
    private static MascotPackage jsonToPackage(String json) {
        MascotPackage packageData = new MascotPackage();
        
        // 简单的JSON解析（用于基本功能）
        packageData.setName(extractJsonString(json, "name"));
        packageData.setVersion(extractJsonString(json, "version"));
        packageData.setAuthor(extractJsonString(json, "author"));
        packageData.setDescription(extractJsonString(json, "description"));
        
        String createTimeStr = extractJsonString(json, "createTime");
        if (createTimeStr != null && !createTimeStr.isEmpty()) {
            try {
                packageData.setCreateTime(java.time.LocalDateTime.parse(createTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } catch (Exception e) {
                log.warning("Failed to parse createTime: " + e.getMessage());
            }
        }
        
        packageData.setPreviewImage(extractJsonString(json, "previewImage"));
        packageData.setImageFiles(extractJsonStringList(json, "imageFiles"));
        packageData.setSoundFiles(extractJsonStringList(json, "soundFiles"));
        packageData.setLanguageFiles(extractJsonStringList(json, "languageFiles"));
        packageData.setSupportedLanguages(extractJsonStringList(json, "supportedLanguages"));
        packageData.setSigned(extractJsonBoolean(json, "signed"));
        
        return packageData;
    }
    
    /**
     * 转义JSON字符串
     * Escape JSON string
     */
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * 列表转JSON数组
     * Convert list to JSON array
     */
    private static String listToJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) json.append(", ");
            json.append("\"").append(escapeJson(list.get(i))).append("\"");
        }
        json.append("]");
        return json.toString();
    }
    
    /**
     * 从JSON中提取字符串值
     * Extract string value from JSON
     */
    private static String extractJsonString(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*(?:\\\\.[^\"]*)*)\"";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1).replace("\\\"", "\"")
                             .replace("\\\\", "\\")
                             .replace("\\n", "\n")
                             .replace("\\r", "\r")
                             .replace("\\t", "\t");
        }
        return null;
    }
    
    /**
     * 从JSON中提取字符串列表
     * Extract string list from JSON
     */
    private static List<String> extractJsonStringList(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\\[(.*?)\\]";
        Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
        java.util.regex.Matcher m = p.matcher(json);
        
        List<String> result = new ArrayList<>();
        if (m.find()) {
            String arrayContent = m.group(1).trim();
            if (!arrayContent.isEmpty()) {
                String[] items = arrayContent.split(",");
                for (String item : items) {
                    String trimmed = item.trim();
                    if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
                        trimmed = trimmed.substring(1, trimmed.length() - 1);
                        trimmed = trimmed.replace("\\\"", "\"")
                                        .replace("\\\\", "\\")
                                        .replace("\\n", "\n")
                                        .replace("\\r", "\r")
                                        .replace("\\t", "\t");
                        result.add(trimmed);
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * 从JSON中提取布尔值
     * Extract boolean value from JSON
     */
    private static boolean extractJsonBoolean(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(true|false)";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(json);
        if (m.find()) {
            return Boolean.parseBoolean(m.group(1));
        }
        return false;
    }
    
    /**
     * 读取包文件信息（仅读取manifest，不安装）
     * Read package file information (manifest only, no installation)
     * 
     * @param packageFile 包文件
     * @return MascotPackage 包信息对象
     * @throws IOException 读取错误
     */
    public static MascotPackage readPackageInfo(File packageFile) throws IOException {
        if (!packageFile.exists() || !packageFile.getName().endsWith(".smp")) {
            throw new IllegalArgumentException("Invalid package file");
        }
        
        try (ZipFile zipFile = new ZipFile(packageFile)) {
            MascotPackage packageData = readManifest(zipFile);
            if (packageData == null || !packageData.validatePackage()) {
                throw new IllegalArgumentException("Invalid package manifest");
            }
            return packageData;
        }
    }
}
