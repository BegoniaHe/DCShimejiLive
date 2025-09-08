import os

def batch_rename_shimeji_images():
    """
    批量重命名当前文件夹中的 Shimeji 动作图片文件。
    """
    # 定义文件名映射关系
    renaming_map = {
        'shime1.png': 'Stand.png',
        'shime2.png': 'WalkLeft.png',
        'shime3.png': 'WalkRight.png',
        'shime4.png': 'Falling.png',
        'shime5.png': 'Resist1.png',
        'shime6.png': 'Resist2.png',
        'shime7.png': 'StruggleHard1.png',
        'shime8.png': 'StruggleHard2.png',
        'shime9.png': 'StruggleHard3.png',
        'shime10.png': 'StruggleHard4.png',
        'shime11.png': 'Sit.png',
        'shime12.png': 'ClimbWallExtend.png',
        'shime13.png': 'GrabWall.png',
        'shime14.png': 'ClimbWallContract.png',
        'shime15.png': 'SitHeadTurn1.png',
        'shime16.png': 'SitHeadTurn2.png',
        'shime17.png': 'SitHeadTurn3.png',
        'shime18.png': 'BounceCompress.png',
        'shime19.png': 'BounceExtend.png',
        'shime20.png': 'CreepExtend.png',
        'shime21.png': 'CreepContract.png',
        'shime22.png': 'JumpStart.png',
        'shime23.png': 'GrabCeiling.png',
        'shime24.png': 'ClimbCeiling1.png',
        'shime25.png': 'ClimbCeiling2.png',
        'shime26.png': 'SitLookUp.png',
        'shime27.png': 'SitHeadTurn4.png',
        'shime28.png': 'SitHeadTurn5.png',
        'shime29.png': 'SitHeadTurn6.png',
        'shime30.png': 'SitLegsUp.png',
        'shime31.png': 'SitLegsDown.png',
        'shime32.png': 'SitDangle1.png',
        'shime33.png': 'SitDangle2.png',
        'shime34.png': 'WalkWithWindow1.png',
        'shime35.png': 'WalkWithWindow2.png',
        'shime36.png': 'StandWithWindow.png',
        'shime37.png': 'ThrowWindow.png',
        'shime38.png': 'PullUpPrepare.png',
        'shime39.png': 'PullUpEffort.png',
        'shime40.png': 'PullUpHalf.png',
        'shime41.png': 'PullUpComplete.png',
        'shime42.png': 'DivideTwist1.png',
        'shime43.png': 'DivideTwist2.png',
        'shime44.png': 'DivideTwist3.png',
        'shime45.png': 'DivideSplit.png',
        'shime46.png': 'DivideComplete.png',
    }

    # 获取当前文件夹路径
    folder_path = '.' 
    renamed_count = 0
    not_found_count = 0

    print("开始批量重命名...\n")

    # 遍历映射关系并重命名文件
    for old_name, new_name in renaming_map.items():
        old_path = os.path.join(folder_path, old_name)
        new_path = os.path.join(folder_path, new_name)

        # 检查旧文件是否存在
        if os.path.exists(old_path):
            try:
                os.rename(old_path, new_path)
                print(f"成功: {old_name} -> {new_name}")
                renamed_count += 1
            except OSError as e:
                print(f"错误: 重命名 {old_name} 时发生错误: {e}")
        else:
            # 文件不存在，打印提示信息
            # print(f"跳过: 文件 {old_name} 不存在")
            not_found_count += 1
            
    print(f"\n重命名完成。")
    print(f"总计 {renamed_count} 个文件被成功重命名。")
    if not_found_count > 0:
        print(f"有 {not_found_count} 个原始文件未在文件夹中找到。")

if __name__ == "__main__":
    batch_rename_shimeji_images()

