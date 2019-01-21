package com.apabi.flow.systemconf.controller;

import com.apabi.flow.book.util.EMailUtil;
import com.apabi.flow.systemconf.dao.SystemConfMapper;
import com.apabi.flow.systemconf.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @Autowired
    SystemConfMapper systemConfMapper;

    //上传文件页面
    @RequestMapping(value = "/index")
    public String uploadFile() {
        return "systemConf/upload";
    }

    //跳转上传文件信息页码
    @RequestMapping(value = "/uploadInfo")
    public String uploadInfo(@RequestParam(value = "toEmail") String toEmail, Model model) {
        model.addAttribute("toEmail", toEmail);
        return "systemConf/uploadInfo";
    }

    //解析xml文件
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public void uploadFile(@RequestParam(value = "files") MultipartFile[] files,
                           @RequestParam(value = "toEmail") String toEmail,
                           @RequestParam(value = "uploadPath") String uploadPath) {
        if (files != null && StringUtils.isNotBlank(uploadPath)
                && StringUtils.isNotBlank(toEmail)) {
            try {
                long start = System.currentTimeMillis();
                log.info("路径：{}下的文件已接收，正在生成。。。", uploadPath);
                //上传文件
                FileUtil.saveMultiFile(uploadPath, files);
                //发送邮件
                EMailUtil eMailUtil = new EMailUtil(systemConfMapper);
                eMailUtil.createSender();
                eMailUtil.sendNoticeMail("路径：" + uploadPath + "下的文件已上传成功", toEmail);
                log.info("路径：{}下的文件已上传成功，通知邮件已发出", uploadPath);
                long end = System.currentTimeMillis();
                log.info(files.length + "个文件上传成功！耗时：" + (end - start) + "毫秒");
            } catch (IOException e) {
                log.warn("上传文件到目录{}时，出现异常{}", uploadPath, e.getMessage());
            }
        }
    }
}
