package com.apabi.flow.systemconf.controller;

import com.apabi.flow.systemconf.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author guanpp
 * @date 2018/11/28 16:01
 * @description
 */
@Controller("upload")
@RequestMapping(value = "/upload")
public class UploadFileController {

    private Logger log = LoggerFactory.getLogger(UploadFileController.class);

    //上传文件页面
    @RequestMapping(value = "/index")
    public String uploadFile() {
        return "systemConf/upload";
    }

    //跳转上传文件信息页码
    @RequestMapping(value = "/uploadInfo")
    public String uploadInfo() {
        return "systemConf/uploadInfo";
    }

    //解析xml文件
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFile(@RequestParam(value = "files", required = false) MultipartFile[] files,
                             @RequestParam(value = "uploadPath", required = false) String uploadPath) {
        if (files != null && StringUtils.isNotBlank(uploadPath)) {
            try {
                long start = System.currentTimeMillis();
                FileUtil.saveMultiFile(uploadPath, files);
                long end = System.currentTimeMillis();
                log.info(files.length + "个文件上传成功！耗时：" + (end - start) + "毫秒");
                return "success";
            } catch (IOException e) {
                log.warn("上传文件到目录{}时，出现异常{}", uploadPath, e.getMessage());
            }
        }
        return "error";
    }
}
