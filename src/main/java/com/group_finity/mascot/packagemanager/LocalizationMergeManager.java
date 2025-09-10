package com.group_finity.mascot.packagemanager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * 本地化文件合并管理器
 * Localization File Merge Manager
 * 
 * @author DCShimeji Team
 */
public class LocalizationMergeManager {
    private static final Logger log = Logger.getLogger(LocalizationMergeManager.class.getName());
    private static final String CONF_FOLDER = "conf";
    private static final String BACKUP_SUFFIX = ".backup";
    private static final String MASCOT_PREFIX_FORMAT = "mascot.%s.%s"; // mascot.{mascotName}.{originalKey}
    
    /**
     * 合并本地化文件到全局配置
     * Merge localization files into global configuration
     * 
     * @param mascotName 桌宠名称
     * @param localizationData 本地化数据
     * @return true if successful, false otherwise
     */
    public static boolean mergeLocalizationFiles(String mascotName, 
                                                Map<String, Properties> localizationData) {
        try {
            File confDir = new File(CONF_FOLDER);
            if (!confDir.exists()) {
                confDir.mkdirs();
            }
            
            // 为每个语言文件进行合并
            for (Map.Entry<String, Properties> entry : localizationData.entrySet()) {
                String languageFile = entry.getKey();
                Properties newProperties = entry.getValue();
                
                if (!mergeLanguageFile(mascotName, languageFile, newProperties)) {
                    log.warning("Failed to merge language file: " + languageFile);
                    return false;
                }
            }
            
            log.info("Successfully merged localization for mascot: " + mascotName);
            return true;
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to merge localization files", e);
            return false;
        }
    }
    
    /**
     * 合并单个语言文件
     * Merge single language file
     * 
     * @param mascotName 桌宠名称
     * @param languageFile 语言文件名
     * @param newProperties 新的属性
     * @return true if successful, false otherwise
     * @throws IOException 如果文件操作失败
     */
    private static boolean mergeLanguageFile(String mascotName, String languageFile, 
                                           Properties newProperties) throws IOException {
        File targetFile = new File(CONF_FOLDER, languageFile);
        File backupFile = new File(CONF_FOLDER, languageFile + BACKUP_SUFFIX);
        
        Properties existingProperties = new Properties();
        
        // 读取现有文件
        if (targetFile.exists()) {
            // 创建备份
            Files.copy(targetFile.toPath(), backupFile.toPath(), 
                      StandardCopyOption.REPLACE_EXISTING);
            
            try (FileInputStream fis = new FileInputStream(targetFile);
                 InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
                existingProperties.load(reader);
            }
        }
        
        // 记录合并信息
        Set<String> addedKeys = new HashSet<>();
        Set<String> conflictKeys = new HashSet<>();
        
        // 合并属性
        for (Object keyObj : newProperties.keySet()) {
            String originalKey = keyObj.toString();
            String value = newProperties.getProperty(originalKey);
            
            // 生成带桌宠前缀的键名
            String prefixedKey = String.format(MASCOT_PREFIX_FORMAT, mascotName, originalKey);
            
            // 检查冲突
            if (existingProperties.containsKey(originalKey)) {
                String existingValue = existingProperties.getProperty(originalKey);
                if (!existingValue.equals(value)) {
                    conflictKeys.add(originalKey);
                    // 使用前缀键名避免冲突
                    existingProperties.setProperty(prefixedKey, value);
                }
            } else {
                // 同时添加原始键名和前缀键名
                existingProperties.setProperty(originalKey, value);
                existingProperties.setProperty(prefixedKey, value);
                addedKeys.add(originalKey);
            }
        }
        
        // 添加合并记录注释
        addMergeRecord(existingProperties, mascotName, addedKeys, conflictKeys);
        
        // 写入合并后的文件
        try (FileOutputStream fos = new FileOutputStream(targetFile);
             OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            existingProperties.store(writer, 
                String.format("Merged localization for mascot: %s at %s", 
                             mascotName, new Date()));
        }
        
        log.info(String.format("Merged %s: added %d keys, %d conflicts resolved", 
                              languageFile, addedKeys.size(), conflictKeys.size()));
        
        return true;
    }
    
    /**
     * 添加合并记录到Properties中
     * Add merge record to Properties
     * 
     * @param props 属性对象
     * @param mascotName 桌宠名称
     * @param addedKeys 添加的键
     * @param conflictKeys 冲突的键
     */
    private static void addMergeRecord(Properties props, String mascotName, 
                                     Set<String> addedKeys, Set<String> conflictKeys) {
        // 添加元数据记录（以特殊前缀标识，不会被正常使用）
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        props.setProperty("_merge.mascot." + mascotName + ".timestamp", timestamp);
        props.setProperty("_merge.mascot." + mascotName + ".added", String.join(",", addedKeys));
        props.setProperty("_merge.mascot." + mascotName + ".conflicts", String.join(",", conflictKeys));
    }
    
    /**
     * 卸载桌宠时移除对应的本地化条目
     * Remove localization entries when uninstalling mascot
     * 
     * @param mascotName 桌宠名称
     * @return true if successful, false otherwise
     */
    public static boolean removeLocalizationEntries(String mascotName) {
        try {
            File confDir = new File(CONF_FOLDER);
            if (!confDir.exists()) return true;
            
            File[] languageFiles = confDir.listFiles((dir, name) -> 
                name.startsWith("language") && name.endsWith(".properties"));
            
            if (languageFiles == null) return true;
            
            for (File langFile : languageFiles) {
                removeMascotEntries(langFile, mascotName);
            }
            
            log.info("Removed localization entries for mascot: " + mascotName);
            return true;
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to remove localization entries", e);
            return false;
        }
    }
    
    /**
     * 从单个语言文件中移除指定桌宠的条目
     * Remove mascot entries from single language file
     * 
     * @param langFile 语言文件
     * @param mascotName 桌宠名称
     * @throws IOException 如果文件操作失败
     */
    private static void removeMascotEntries(File langFile, String mascotName) throws IOException {
        Properties props = new Properties();
        
        // 读取现有文件
        try (FileInputStream fis = new FileInputStream(langFile);
             InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            props.load(reader);
        }
        
        // 获取合并记录
        String addedKeysStr = props.getProperty("_merge.mascot." + mascotName + ".added", "");
        String conflictKeysStr = props.getProperty("_merge.mascot." + mascotName + ".conflicts", "");
        
        Set<String> addedKeys = addedKeysStr.isEmpty() ? 
            new HashSet<>() : new HashSet<>(Arrays.asList(addedKeysStr.split(",")));
        Set<String> conflictKeys = conflictKeysStr.isEmpty() ? 
            new HashSet<>() : new HashSet<>(Arrays.asList(conflictKeysStr.split(",")));
        
        // 移除添加的键（只移除没有冲突的）
        for (String key : addedKeys) {
            if (!conflictKeys.contains(key)) {
                props.remove(key);
            }
        }
        
        // 移除所有带前缀的键
        String prefix = "mascot." + mascotName + ".";
        props.entrySet().removeIf(entry -> entry.getKey().toString().startsWith(prefix));
        
        // 移除合并记录
        props.remove("_merge.mascot." + mascotName + ".timestamp");
        props.remove("_merge.mascot." + mascotName + ".added");
        props.remove("_merge.mascot." + mascotName + ".conflicts");
        
        // 写回文件
        try (FileOutputStream fos = new FileOutputStream(langFile);
             OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            props.store(writer, "Removed entries for mascot: " + mascotName);
        }
    }
    
    /**
     * 获取桌宠专用的本地化值
     * Get mascot-specific localized value
     * 
     * @param mascotName 桌宠名称
     * @param key 键名
     * @param langProps 语言属性
     * @return 本地化值
     */
    public static String getLocalizedValue(String mascotName, String key, Properties langProps) {
        // 先尝试获取桌宠专用的值
        String prefixedKey = String.format(MASCOT_PREFIX_FORMAT, mascotName, key);
        String value = langProps.getProperty(prefixedKey);
        
        if (value != null) {
            return value;
        }
        
        // 回退到通用值
        return langProps.getProperty(key);
    }
    
    /**
     * 检查本地化文件的冲突情况
     * Check localization file conflicts
     * 
     * @param mascotName 桌宠名称
     * @param newLocalization 新的本地化数据
     * @return 冲突报告
     */
    public static LocalizationConflictReport checkConflicts(String mascotName, 
                                                           Map<String, Properties> newLocalization) {
        LocalizationConflictReport report = new LocalizationConflictReport();
        
        File confDir = new File(CONF_FOLDER);
        if (!confDir.exists()) return report;
        
        for (Map.Entry<String, Properties> entry : newLocalization.entrySet()) {
            String languageFile = entry.getKey();
            Properties newProps = entry.getValue();
            
            File existingFile = new File(confDir, languageFile);
            if (existingFile.exists()) {
                try {
                    Properties existingProps = new Properties();
                    try (FileInputStream fis = new FileInputStream(existingFile);
                         InputStreamReader reader = new InputStreamReader(fis, "UTF-8")) {
                        existingProps.load(reader);
                    }
                    
                    // 检查键冲突
                    for (Object keyObj : newProps.keySet()) {
                        String key = keyObj.toString();
                        if (existingProps.containsKey(key)) {
                            String existingValue = existingProps.getProperty(key);
                            String newValue = newProps.getProperty(key);
                            
                            if (!existingValue.equals(newValue)) {
                                report.addConflict(languageFile, key, existingValue, newValue);
                            }
                        }
                    }
                    
                } catch (IOException e) {
                    log.warning("Failed to check conflicts for " + languageFile + ": " + e.getMessage());
                }
            }
        }
        
        return report;
    }
}
