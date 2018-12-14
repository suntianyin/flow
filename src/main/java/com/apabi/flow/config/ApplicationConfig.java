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

    //版权协议上传路径
    @Value("${upAuthFile}")
    private String upAuthFile;

    //epub文件存储路径
    @Value("${uploadEpub}")
    private String uploadEpub;

    //cebx文件存储路径
    @Value("${uploadCebx}")
    private String uploadCebx;

    //流式内容检查结果存放路径
    @Value("${bookDetect}")
    private String bookDetect;

    //获取流式内容获取批次
    @Value("${batchSize}")
    private Integer batchSize;

    //线程倍数
    @Value("${threadTime}")
    private Integer threadTime;

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

    public String getBookDetect() {
        return bookDetect;
    }

    public void setBookDetect(String bookDetect) {
        this.bookDetect = bookDetect;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Integer getThreadTime() {
        return threadTime;
    }

    public void setThreadTime(Integer threadTime) {
        this.threadTime = threadTime;
    }

    public String getUpAuthFile() {
        return upAuthFile;
    }

    public void setUpAuthFile(String upAuthFile) {
        this.upAuthFile = upAuthFile;
    }
}
