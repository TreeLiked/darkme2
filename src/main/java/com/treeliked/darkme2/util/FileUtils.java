package com.treeliked.darkme2.util;

/**
 * 文件工具类
 *
 * @author lqs2
 * @date 2018-12-18, Tue
 */
public class FileUtils {


    public static String getSuffixByName(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
