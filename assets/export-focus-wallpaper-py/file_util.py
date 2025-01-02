#!/usr/bin/python
# -*- coding: UTF-8-*-
import os


#   获取windows下的用户目录
def get_user_home():
    homeDir = os.environ['HOMEDRIVE']
    homePath = os.environ['HOMEPATH']
    return homeDir + homePath


#   获取锁屏目录
def get_wallpaper_dir():
    package_dir = get_user_home() + '\\AppData\\Local\\Packages\\'
    list1 = os.listdir(package_dir)
    for filename in list1:
        if 'Microsoft.Windows.ContentDeliveryManager' in filename:
            return package_dir + filename + '\\LocalState\\Assets'
    return ''


