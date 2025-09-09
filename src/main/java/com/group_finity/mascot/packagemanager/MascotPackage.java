package com.group_finity.mascot.packagemanager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 桌宠包数据类
 * Mascot Package Data Class
 * 
 * @author DCShimeji Team
 */
public class MascotPackage {
    private String name;                    // 包名
    private String version;                 // 版本
    private String author;                  // 作者
    private String description;             // 描述
    private LocalDateTime createTime;       // 创建时间
    private String previewImage;            // 预览图片路径
    private List<String> imageFiles;        // 图片文件列表
    private List<String> soundFiles;        // 音频文件列表
    private List<String> languageFiles;     // 本地化文件列表
    private List<String> supportedLanguages; // 支持的语言列表
    private Map<String, Object> metadata;   // 其他元数据
    private boolean signed;                 // 是否已签名
    
    /**
     * 默认构造函数
     */
    public MascotPackage() {
        this.createTime = LocalDateTime.now();
        this.version = "1.0";
        this.signed = false;
    }
    
    // Getter and Setter methods
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public String getPreviewImage() {
        return previewImage;
    }
    
    public void setPreviewImage(String previewImage) {
        this.previewImage = previewImage;
    }
    
    public List<String> getImageFiles() {
        return imageFiles;
    }
    
    public void setImageFiles(List<String> imageFiles) {
        this.imageFiles = imageFiles;
    }
    
    public List<String> getSoundFiles() {
        return soundFiles;
    }
    
    public void setSoundFiles(List<String> soundFiles) {
        this.soundFiles = soundFiles;
    }
    
    public List<String> getLanguageFiles() {
        return languageFiles;
    }
    
    public void setLanguageFiles(List<String> languageFiles) {
        this.languageFiles = languageFiles;
    }
    
    public List<String> getSupportedLanguages() {
        return supportedLanguages;
    }
    
    public void setSupportedLanguages(List<String> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public boolean isSigned() {
        return signed;
    }
    
    public void setSigned(boolean signed) {
        this.signed = signed;
    }
    
    /**
     * 验证包的完整性
     * Validate package integrity
     * 
     * @return true if package is valid, false otherwise
     */
    public boolean validatePackage() {
        // 检查必需文件是否存在
        boolean basicValidation = name != null && !name.isEmpty() && 
                                 imageFiles != null && !imageFiles.isEmpty();
        
        // 检查是否包含基本的language.properties文件
        boolean hasBasicLanguage = languageFiles == null || languageFiles.isEmpty() ||
                                  languageFiles.stream().anyMatch(file -> file.endsWith("language.properties"));
        
        return basicValidation && hasBasicLanguage;
    }
    
    @Override
    public String toString() {
        return String.format("MascotPackage{name='%s', version='%s', author='%s', " +
                           "imageFiles=%d, soundFiles=%d, languageFiles=%d, languages=%s}",
                           name, version, author,
                           imageFiles != null ? imageFiles.size() : 0,
                           soundFiles != null ? soundFiles.size() : 0,
                           languageFiles != null ? languageFiles.size() : 0,
                           supportedLanguages);
    }
}
