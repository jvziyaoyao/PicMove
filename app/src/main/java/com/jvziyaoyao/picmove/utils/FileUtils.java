package com.jvziyaoyao.picmove.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-02-28 20:58
 **/
public class FileUtils {

    public static final String TAG = FileUtils.class.getSimpleName();

    /**
     * 复制单个文件
     *
     * @param oldPath$Name String 原文件路径+文件名 如：data/user/0/com.test/files/abc.txt
     * @param newPath$Name String 复制后路径+文件名 如：data/user/0/com.test/cache/abc.txt
     * @return <code>true</code> if and only if the file was copied;
     */
    public static boolean copyFile(String oldPath$Name, String newPath$Name) {
        try {
            File oldFile = new File(oldPath$Name);
            if (!oldFile.exists()) {
                Log.e(TAG, "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.e(TAG, "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.e(TAG, "copyFile:  oldFile cannot read.");
                return false;
            }

            FileInputStream fileInputStream = new FileInputStream(oldPath$Name);
            FileOutputStream fileOutputStream = new FileOutputStream(newPath$Name);
            byte[] buffer = new byte[1024];
            int len;
            while (-1 != (len = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, len);
                fileOutputStream.flush();
            }
            fileInputStream.close();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 复制文件夹及其中的文件
     *
     * @param oldPath String 原文件夹路径 如：data/user/0/com.test/files
     * @param newPath String 复制后的路径 如：data/user/0/com.test/cache
     * @return <code>true</code> if and only if the directory and files were copied;
     */
    public static boolean copyFolder(String oldPath, String newPath) {
        try {
            File newFile = new File(newPath);
            if (!newFile.exists()) {
                if (!newFile.mkdirs()) {
                    Log.e(TAG, "copyFolder: cannot create directory.");
                    return false;
                }
            }
            File oldFile = new File(oldPath);
            String[] files = oldFile.list();
            File temp;
            for (String file : files) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file);
                } else {
                    temp = new File(oldPath + File.separator + file);
                }

                if (temp.isDirectory()) {   //如果是子文件夹
                    copyFolder(oldPath + "/" + file, newPath + "/" + file);
                } else if (!temp.exists()) {
                    Log.e(TAG, "copyFolder:  oldFile not exist.");
                    return false;
                } else if (!temp.isFile()) {
                    Log.e(TAG, "copyFolder:  oldFile not file.");
                    return false;
                } else if (!temp.canRead()) {
                    Log.e(TAG, "copyFolder:  oldFile cannot read.");
                    return false;
                } else {
                    FileInputStream fileInputStream = new FileInputStream(temp);
                    FileOutputStream fileOutputStream = new FileOutputStream(newPath + "/" + temp.getName());
                    byte[] buffer = new byte[1024];
                    int byteRead;
                    while ((byteRead = fileInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, byteRead);
                        fileOutputStream.flush();
                    }
                    fileInputStream.close();
                    fileOutputStream.close();
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
