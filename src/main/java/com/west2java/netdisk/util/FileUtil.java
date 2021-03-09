package com.west2java.netdisk.util;


import java.io.File;
import java.text.SimpleDateFormat;
public class FileUtil {
    private static String filepath = "/netdisk";//文件存储的根路径
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    public static File transfer(com.west2java.netdisk.entity.File entity) {
        String date = sdf.format(entity.getUploadTime());
        File file = new File(filepath, date + entity.getName());
        return file;
    }
}
