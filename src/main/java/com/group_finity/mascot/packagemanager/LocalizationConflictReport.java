package com.group_finity.mascot.packagemanager;

import java.util.*;

/**
 * 本地化冲突报告
 * Localization Conflict Report
 * 
 * @author DCShimeji Team
 */
public class LocalizationConflictReport {
    private Map<String, List<ConflictEntry>> conflicts = new HashMap<>();
    
    /**
     * 冲突条目数据类
     * Conflict entry data class
     */
    public static class ConflictEntry {
        public final String key;
        public final String existingValue;
        public final String newValue;
        
        public ConflictEntry(String key, String existingValue, String newValue) {
            this.key = key;
            this.existingValue = existingValue;
            this.newValue = newValue;
        }
        
        @Override
        public String toString() {
            return String.format("Key: %s\n  Existing: %s\n  New: %s", 
                               key, existingValue, newValue);
        }
    }
    
    /**
     * 添加冲突条目
     * Add conflict entry
     * 
     * @param languageFile 语言文件名
     * @param key 冲突的键
     * @param existingValue 现有值
     * @param newValue 新值
     */
    public void addConflict(String languageFile, String key, String existingValue, String newValue) {
        conflicts.computeIfAbsent(languageFile, k -> new ArrayList<>())
                 .add(new ConflictEntry(key, existingValue, newValue));
    }
    
    /**
     * 检查是否存在冲突
     * Check if conflicts exist
     * 
     * @return true if conflicts exist, false otherwise
     */
    public boolean hasConflicts() {
        return !conflicts.isEmpty();
    }
    
    /**
     * 获取所有冲突
     * Get all conflicts
     * 
     * @return conflicts map
     */
    public Map<String, List<ConflictEntry>> getConflicts() {
        return conflicts;
    }
    
    /**
     * 获取冲突总数
     * Get total conflict count
     * 
     * @return total number of conflicts
     */
    public int getTotalConflicts() {
        return conflicts.values().stream()
                        .mapToInt(List::size)
                        .sum();
    }
    
    /**
     * 获取格式化的冲突报告
     * Get formatted conflict report
     * 
     * @return formatted report string
     */
    public String getFormattedReport() {
        if (!hasConflicts()) {
            return "无冲突 / No conflicts found";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("本地化文件冲突报告 / Localization Conflicts Report:\n");
        sb.append("总计 / Total: ").append(getTotalConflicts()).append(" conflicts\n\n");
        
        for (Map.Entry<String, List<ConflictEntry>> entry : conflicts.entrySet()) {
            sb.append("文件 / File: ").append(entry.getKey()).append(" (")
              .append(entry.getValue().size()).append(" conflicts)\n");
            for (ConflictEntry conflict : entry.getValue()) {
                sb.append("  ").append(conflict.toString()).append("\n");
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * 获取简要报告
     * Get brief report
     * 
     * @return brief report string
     */
    public String getBriefReport() {
        if (!hasConflicts()) {
            return "无冲突 / No conflicts";
        }
        
        return String.format("发现 %d 个冲突，涉及 %d 个语言文件 / Found %d conflicts in %d language files",
                           getTotalConflicts(), conflicts.size(), getTotalConflicts(), conflicts.size());
    }
}
