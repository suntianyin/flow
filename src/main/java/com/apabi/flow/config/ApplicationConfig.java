package com.apabi.flow.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ApplicationConfig {

    //cebx命令
    @Value("${cebxHtmlExe}")
    private String cebxHtmlExe;

    //cebx解析目标文件夹
    @Value("${targetCebxDir}")
    private String targetCebxDir;

    //样式文件存储路径
    @Value("${styleUrl}")
    private String styleUrl;

    //xml文件存储路径
    @Value("${uploadXml}")
    private String uploadXml;

    //epub文件存储路径
    @Value("${uploadEpub}")
    private String uploadEpub;

    //cebx文件存储路径
    @Value("${uploadCebx}")
    private String uploadCebx;

    public String getCebxHtmlExe() {
        return cebxHtmlExe;
    }

    public void setCebxHtmlExe(String cebxHtmlExe) {
        this.cebxHtmlExe = cebxHtmlExe;
    }

    public String getTargetCebxDir() {
        return targetCebxDir;
    }

    public void setTargetCebxDir(String targetCebxDir) {
        this.targetCebxDir = targetCebxDir;
    }

    public String getStyleUrl() {
        return styleUrl;
    }

    public void setStyleUrl(String styleUrl) {
        this.styleUrl = styleUrl;
    }

    public String getUploadXml() {
        return uploadXml;
    }

    public void setUploadXml(String uploadXml) {
        this.uploadXml = uploadXml;
    }

    public String getUploadEpub() {
        return uploadEpub;
    }

    public void setUploadEpub(String uploadEpub) {
        this.uploadEpub = uploadEpub;
    }

    public String getUploadCebx() {
        return uploadCebx;
    }

    public void setUploadCebx(String uploadCebx) {
        this.uploadCebx = uploadCebx;
    }
}
