package com.apabi.flow.systemconf.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.apabi.flow.processing.service.impl.BibliothecaServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author guanpp
 * @date 2018/11/28 16:28
 * @description
 */
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

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

    //根据url下载文件，到指定目录
    public static void download4Url(String url4Net, String dir, String fileName) {
        if (StringUtils.isNotBlank(url4Net)
                && StringUtils.isNotBlank(dir)
                && StringUtils.isNotBlank(fileName)) {
            URL url;
            OutputStream os = null;
            InputStream is = null;
            try {
                url = new URL(url4Net);
                // 打开连接
                URLConnection con = url.openConnection();
                //设置请求超时为5s
                con.setConnectTimeout(5 * 1000);
                // 输入流
                is = con.getInputStream();
                // 1K的数据缓冲
                byte[] bs = new byte[1024];
                // 读取到的数据长度
                int len;
                // 输出的文件流
                File sf = new File(dir);
                if (!sf.exists()) {
                    sf.mkdirs();
                }
                os = new FileOutputStream(sf.getPath() + File.separator + fileName);
                // 开始读取
                while ((len = is.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }
            } catch (IOException e) {
                logger.warn("下载文件{}，时出现异常{}", fileName, e.getMessage());
            } finally {
                try {
                    if (os != null) os.close();
                    if (is != null) is.close();
                } catch (IOException e) {
                    logger.warn("下载文件{}，时出现异常{}", fileName, e.getMessage());
                }
            }
        } else {
            logger.warn("无法下载文件{}，请检查URL、文件路径或文件名称", fileName);
        }
    }
}

