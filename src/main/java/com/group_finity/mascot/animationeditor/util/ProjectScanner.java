package com.group_finity.mascot.animationeditor.util;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 项目扫描工具类
 * Project Scanner Utility
 * 
 * @author DCShimeji Team
 */
public class ProjectScanner {
    
    /**
     * 扫描img目录下的所有子文件夹作为项目
     */
    public static List<File> scanProjects(File imgDirectory) {
        List<File> projects = new ArrayList<>();
        
        if (!imgDirectory.exists() || !imgDirectory.isDirectory()) {
            return projects;
        }
        
        File[] subdirs = imgDirectory.listFiles(File::isDirectory);
        if (subdirs != null) {
            projects.addAll(Arrays.asList(subdirs));
        }
        
        return projects;
    }
    
    /**
     * 扫描项目目录下的图片文件
     */
    public static List<File> scanImageFiles(File projectDirectory) {
        List<File> imageFiles = new ArrayList<>();
        
        if (!projectDirectory.exists() || !projectDirectory.isDirectory()) {
            return imageFiles;
        }
        
        scanImageFilesRecursive(projectDirectory, imageFiles);
        return imageFiles;
    }
    
    private static void scanImageFilesRecursive(File directory, List<File> imageFiles) {
        File[] files = directory.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                scanImageFilesRecursive(file, imageFiles);
            } else if (isImageFile(file)) {
                imageFiles.add(file);
            }
        }
    }
    
    /**
     * 检查文件是否为图片文件
     */
    public static boolean isImageFile(File file) {
        if (!file.isFile()) return false;
        
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || 
               name.endsWith(".jpeg") || name.endsWith(".gif") ||
               name.endsWith(".bmp") || name.endsWith(".tiff");
    }
    
    /**
     * 获取项目的配置文件路径
     */
    public static File getConfigFile(File projectDirectory) {
        return new File(projectDirectory, "conf/actions.xml");
    }
    
    /**
     * 检查项目是否有效（包含配置文件）
     */
    public static boolean isValidProject(File projectDirectory) {
        return getConfigFile(projectDirectory).exists();
    }
    
    /**
     * 获取第一个有效的项目
     */
    public static File getFirstValidProject(File imgDirectory) {
        List<File> projects = scanProjects(imgDirectory);
        for (File project : projects) {
            if (isValidProject(project)) {
                return project;
            }
        }
        return null;
    }
    
    /**
     * 获取下一个项目
     */
    public static File getNextProject(File imgDirectory, File currentProject) {
        List<File> projects = scanProjects(imgDirectory);
        if (projects.isEmpty()) return null;
        
        int currentIndex = -1;
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).equals(currentProject)) {
                currentIndex = i;
                break;
            }
        }
        
        // 循环到下一个项目
        int nextIndex = (currentIndex + 1) % projects.size();
        return projects.get(nextIndex);
    }
    
    /**
     * 获取上一个项目
     */
    public static File getPreviousProject(File imgDirectory, File currentProject) {
        List<File> projects = scanProjects(imgDirectory);
        if (projects.isEmpty()) return null;
        
        int currentIndex = -1;
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).equals(currentProject)) {
                currentIndex = i;
                break;
            }
        }
        
        // 循环到上一个项目
        int prevIndex = currentIndex <= 0 ? projects.size() - 1 : currentIndex - 1;
        return projects.get(prevIndex);
    }
}
