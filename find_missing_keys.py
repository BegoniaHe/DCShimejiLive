#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
扫描src目录下的Java文件，查找ResourceBundle.getString()调用中使用的资源键，
并检查这些键是否在properties文件中存在。将缺失的键添加到language.properties中。

Scans Java files in src directory for resource keys used in ResourceBundle.getString() calls,
checks if these keys exist in properties files, and adds missing keys to language.properties.
"""

import os
import re
import glob

def find_java_files(directory):
    """递归查找所有Java文件"""
    java_files = []
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith('.java'):
                java_files.append(os.path.join(root, file))
    return java_files

def extract_resource_keys_from_file(file_path):
    """从Java文件中提取资源键"""
    resource_keys = set()
    
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
    except UnicodeDecodeError:
        try:
            with open(file_path, 'r', encoding='gbk') as f:
                content = f.read()
        except:
            print(f"Warning: Could not read file {file_path}")
            return resource_keys
    
    # 匹配 ResourceBundle.getString("key") 或 languageBundle.getString("key")
    # 也匹配变量.getString("key")的模式
    patterns = [
        r'\.getString\s*\(\s*"([^"]+)"\s*\)',  # .getString("key")
        r'\.getString\s*\(\s*\'([^\']+)\'\s*\)',  # .getString('key')
        r'ResourceBundle\.getString\s*\(\s*"([^"]+)"\s*\)',  # ResourceBundle.getString("key")
        r'languageBundle\.getString\s*\(\s*"([^"]+)"\s*\)',  # languageBundle.getString("key")
    ]
    
    for pattern in patterns:
        matches = re.findall(pattern, content)
        resource_keys.update(matches)
    
    return resource_keys

def load_properties_keys(properties_file):
    """从properties文件加载已存在的键"""
    existing_keys = set()
    
    if not os.path.exists(properties_file):
        print(f"Warning: Properties file {properties_file} does not exist")
        return existing_keys
    
    try:
        with open(properties_file, 'r', encoding='utf-8') as f:
            for line in f:
                line = line.strip()
                # 跳过注释和空行
                if line and not line.startswith('#') and '=' in line:
                    key = line.split('=', 1)[0].strip()
                    existing_keys.add(key)
    except UnicodeDecodeError:
        try:
            with open(properties_file, 'r', encoding='gbk') as f:
                for line in f:
                    line = line.strip()
                    if line and not line.startswith('#') and '=' in line:
                        key = line.split('=', 1)[0].strip()
                        existing_keys.add(key)
        except Exception as e:
            print(f"Error reading properties file {properties_file}: {e}")
    
    return existing_keys

def add_missing_keys_to_properties(missing_keys, properties_file):
    """将缺失的键添加到properties文件"""
    if not missing_keys:
        print("No missing keys to add.")
        return
    
    # 确保properties文件存在
    if not os.path.exists(properties_file):
        print(f"Creating new properties file: {properties_file}")
        with open(properties_file, 'w', encoding='utf-8') as f:
            f.write("# Resource keys\n")
    
    # 读取现有内容
    try:
        with open(properties_file, 'r', encoding='utf-8') as f:
            existing_content = f.read()
    except UnicodeDecodeError:
        with open(properties_file, 'r', encoding='gbk') as f:
            existing_content = f.read()
    
    # 添加缺失的键
    new_entries = []
    for key in sorted(missing_keys):
        # 为键生成一个合理的默认值
        default_value = generate_default_value(key)
        new_entries.append(f"{key} = {default_value}")
    
    # 如果文件不以换行符结尾，添加一个换行符
    if existing_content and not existing_content.endswith('\n'):
        existing_content += '\n'
    
    # 添加新的条目
    new_content = existing_content + '\n# Missing keys added automatically\n' + '\n'.join(new_entries) + '\n'
    
    try:
        with open(properties_file, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Added {len(missing_keys)} missing keys to {properties_file}")
    except Exception as e:
        print(f"Error writing to properties file {properties_file}: {e}")

def generate_default_value(key):
    """为资源键生成合理的默认值"""
    # 一些常见模式的默认值
    default_mappings = {
        'ValidationInProgress': 'Validating license key...',
        'LicenseActivated': 'License activated successfully',
        'LicenseDeactivated': 'License deactivated',
        'InvalidLicenseKey': 'Invalid license key',
        'LicenseExpired': 'License has expired',
        'KeyGenerationFailed': 'Failed to generate key',
        'KeyGenerationSuccess': 'Key generated successfully',
        'ActivationFailed': 'License activation failed',
        'DeactivationFailed': 'License deactivation failed',
        'PleaseEnterValidKey': 'Please enter a valid license key',
        'LicenseKeyEmpty': 'License key cannot be empty',
        'FeatureNotAvailable': 'This feature is not available',
        'AccessDenied': 'Access denied',
        'Error': 'Error',
        'Warning': 'Warning',
        'Info': 'Information',
        'Success': 'Success',
    }
    
    # 如果有直接映射，使用它
    if key in default_mappings:
        return default_mappings[key]
    
    # 根据键名生成默认值
    if 'Error' in key or 'Failed' in key:
        return f"Error: {key.replace('Error', '').replace('Failed', '').replace('Message', '')}"
    elif 'Success' in key or 'Successfully' in key:
        return f"{key.replace('Success', '').replace('Successfully', '')} completed successfully"
    elif 'Confirm' in key:
        return f"Please confirm {key.replace('Confirm', '').lower()}"
    elif 'Message' in key:
        return key.replace('Message', '').replace('Error', 'Error: ')
    elif key.endswith('Title'):
        return key.replace('Title', '')
    elif key.endswith('Label'):
        return key.replace('Label', '')
    else:
        # 将驼峰命名转换为普通文本
        words = re.sub('([A-Z])', r' \1', key).strip().split()
        return ' '.join(words)

def main():
    """主函数"""
    print("Scanning for missing resource keys...")
    
    # 配置路径
    src_directory = "./src"
    properties_files = [
        "./conf/language.properties",
        "./conf/language_en.properties",
        "./conf/language_zh.properties"
    ]
    
    # 检查src目录是否存在
    if not os.path.exists(src_directory):
        print(f"Error: Source directory {src_directory} does not exist")
        return
    
    # 查找所有Java文件
    java_files = find_java_files(src_directory)
    print(f"Found {len(java_files)} Java files to scan")
    
    # 提取所有资源键
    all_resource_keys = set()
    for java_file in java_files:
        keys = extract_resource_keys_from_file(java_file)
        if keys:
            print(f"Found {len(keys)} resource keys in {java_file}")
            for key in keys:
                print(f"  - {key}")
            all_resource_keys.update(keys)
    
    print(f"\nTotal unique resource keys found: {len(all_resource_keys)}")
    
    # 加载所有properties文件中的现有键
    all_existing_keys = set()
    for props_file in properties_files:
        existing_keys = load_properties_keys(props_file)
        all_existing_keys.update(existing_keys)
        print(f"Loaded {len(existing_keys)} keys from {props_file}")
    
    print(f"Total existing keys in all properties files: {len(all_existing_keys)}")
    
    # 找出缺失的键
    missing_keys = all_resource_keys - all_existing_keys
    
    print(f"\nMissing keys ({len(missing_keys)}):")
    for key in sorted(missing_keys):
        print(f"  - {key}")
    
    if missing_keys:
        # 将缺失的键添加到主properties文件
        main_properties_file = "./conf/language.properties"
        add_missing_keys_to_properties(missing_keys, main_properties_file)
        
        print(f"\nMissing keys have been added to {main_properties_file}")
        print("Please review and update the default values as needed.")
        print("You may also want to add translations to other language files:")
        for props_file in properties_files[1:]:  # 跳过主文件
            print(f"  - {props_file}")
    else:
        print("\nAll resource keys are present in the properties files!")

if __name__ == "__main__":
    main()
