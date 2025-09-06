import struct
import os

filepath = 'c:/Users/Begonia/Documents/GitHub/DCShimejiLive/img/icon.ico'
print(f'分析文件: {filepath}')
print(f'文件大小: {os.path.getsize(filepath)} bytes')

try:
    with open(filepath, 'rb') as f:
        # 读取ICO头部
        header = f.read(6)
        if len(header) < 6:
            print('文件太小')
            exit()
        
        reserved, type_flag, count = struct.unpack('<HHH', header)
        print(f'Reserved: {reserved} (应该为0)')
        print(f'Type: {type_flag} (1=ICO, 2=CUR)') 
        print(f'图标数量: {count}')
        
        if reserved != 0:
            print('⚠ Reserved字段不为0，可能有问题')
        if type_flag != 1:
            print('⚠ 不是标准ICO格式')
        if count == 0:
            print('⚠ 没有图标数据')
            
        # 读取图标目录
        for i in range(min(count, 10)):  # 最多显示10个
            entry = f.read(16)
            if len(entry) < 16:
                print(f'图标 {i+1}: 目录项数据不完整')
                break
                
            w, h, colors, res, planes, bpp, size, offset = struct.unpack('<BBBBHHII', entry)
            width = 256 if w == 0 else w
            height = 256 if h == 0 else h
            
            print(f'图标 {i+1}: {width}x{height}, {bpp}bit, {size}bytes, 偏移{offset}')
            
            # 检查常见问题
            if width not in [16, 24, 32, 48, 64, 128, 256]:
                print(f'  ⚠ 非标准尺寸 {width}x{height}')
            if bpp not in [1, 4, 8, 16, 24, 32]:
                print(f'  ⚠ 非标准位深度 {bpp}')
            if size == 0:
                print(f'  ⚠ 图标数据大小为0')
            if offset >= os.path.getsize(filepath):
                print(f'  ⚠ 数据偏移超出文件大小')
            
            # 检查数据大小是否合理
            expected_size = width * height * (bpp // 8) if bpp >= 8 else width * height * bpp // 8
            if size > expected_size * 2:  # 允许一些压缩开销
                print(f'  ⚠ 数据大小异常大 (期望约{expected_size}字节)')
                
        # 系统托盘图标建议检查
        print('\n=== 系统托盘兼容性分析 ===')
        if count == 1:
            print('⚠ 只有1个尺寸，建议包含多个尺寸以支持高DPI显示')
            print('  推荐尺寸：16x16, 20x20, 24x24, 32x32')
        
        # 检查是否有16x16图标
        has_16x16 = False
        f.seek(6)  # 回到图标目录开始
        for i in range(count):
            entry = f.read(16)
            if len(entry) >= 16:
                w, h = struct.unpack('<BB', entry[:2])
                width = 256 if w == 0 else w
                height = 256 if h == 0 else h
                if width == 16 and height == 16:
                    has_16x16 = True
                    break
        
        if not has_16x16:
            print('⚠ 缺少16x16尺寸，这是系统托盘的标准尺寸')
        
        print('\n=== 修复建议 ===')
        print('1. 创建包含多个尺寸的ICO文件 (16x16, 20x20, 24x24, 32x32)')
        print('2. 确保16x16版本使用较少的颜色以减小文件大小')
        print('3. 使用专业的图标编辑工具重新生成ICO文件')
        print('4. 测试在不同DPI设置下的显示效果')
                
except Exception as e:
    print(f'错误: {e}')