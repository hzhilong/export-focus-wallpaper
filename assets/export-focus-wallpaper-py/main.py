#!/usr/bin/python
# -*- coding: UTF-8-*-
import os
import random
import shutil
import string
import time

from PIL import Image

import file_util
import log_util as Log

if __name__ == '__main__':
    wallpaper_dir = file_util.get_wallpaper_dir()
    if '' == wallpaper_dir:
        print('未找到锁屏目录')
    else:
        print('锁屏目录：'+wallpaper_dir)
        extract_dir = file_util.get_user_home() + "\\Pictures\\Win10锁屏壁纸"
        extract_dir = extract_dir.decode('utf-8').encode('gbk')
        if not os.path.exists(extract_dir):
            os.mkdir(extract_dir)
        w_file_list = os.listdir(wallpaper_dir)
        for wallpaper in w_file_list:
            src_file_path = wallpaper_dir + '\\' + wallpaper
            if os.path.isfile(src_file_path):
                Log.info("读取：" + src_file_path)
                Log.info("大小：" + str(os.path.getsize(src_file_path)))
                try:
                    im = Image.open(src_file_path)
                    Log.info("格式："+im.format.lower())
                    Log.info("分辨率："+str(im.size))
                    if im.size[0] == 1080 or im.size[1] == 1080:
                        Log.info("找到锁屏壁纸")
                        new_name = time.strftime("%Y%m%d_%H%M%S", time.localtime()) \
                                + '_' + ''.join(random.sample(string.ascii_letters + string.digits, 4))\
                                + '.'+im.format.lower()
                        Log.info("复制到："+extract_dir+'\\'+new_name)
                        shutil.copy(src_file_path, extract_dir+'\\'+new_name)
                except BaseException as e:
                    print(repr(e))
                    pass
        os.startfile(extract_dir)



