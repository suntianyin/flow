package com.apabi.flow.systemconf.util;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author guanpp
 * @date 2018/11/28 16:28
 * @description
 */
public class FileUtil {

    /**
     * 在basePath下保存上传的文件夹
     *
     * @param basePath
     * @param files
     */
    public static void saveMultiFile(String basePath, MultipartFile[] files) throws IOException {
        if (files == null || files.length == 0) {
            return;
        }
        if (basePath.endsWith(File.separator)) {
            basePath = basePath.substring(0, basePath.length() - 1);
        }
        for (MultipartFile file : files) {
            String filePath = basePath + File.separator + file.getOriginalFilename();
            makeDir(filePath);
            File dest = new File(filePath);
            file.transferTo(dest);
        }
    }

    /**
     * 确保目录存在，不存在则创建
     *
     * @param filePath
     */
    private static void makeDir(String filePath) {
        if (filePath.lastIndexOf("/") > 0) {
            String dirPath = filePath.substring(0, filePath.lastIndexOf("/"));
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }
}

