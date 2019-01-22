package com.apabi.flow.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties
public class ApplicationConfig {

    //pdfDir大目录
    @Value("${pdfDir}")
    private String pdfDir;

    //CarbonExe
    @Value("${CarbonExe}")
    private String carbonExe;

    //cebx命令
    @Value("${cebxHtmlExe}")
    private String cebxHtmlExe;

    //cebx加密/解密工具
    @Value("${cebxCryptExe}")
    private String cebxCryptExe;

    //maker的DLL
    @Value("${cebxMaker}")
    private static String cebxMaker;

    //书目解析
    @Value("${copyRightExtractExe}")
    private String copyRightExtractExe;

    //书目解析
    @Value("${targetCopyRightDir}")
    private String targetCopyRightDir;

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

    //存放待发送的邮件
    @Value("${email}")
    private String email;

    //获取流式内容获取批次
    @Value("${batchSize}")
    private Integer batchSize;

    //线程倍数
    @Value("${threadTime}")
    private Integer threadTime;

    public String getPdfDir() {
        return pdfDir;
    }

    public void setPdfDir(String pdfDir) {
        this.pdfDir = pdfDir;
    }

    public String getCarbonExe() {
        return carbonExe;
    }

    public void setCarbonExe(String carbonExe) {
        this.carbonExe = carbonExe;
    }

    public String getCebxHtmlExe() {
        return cebxHtmlExe;
    }

    public void setCebxHtmlExe(String cebxHtmlExe) {
        this.cebxHtmlExe = cebxHtmlExe;
    }

    public String getCebxCryptExe() {
        return cebxCryptExe;
    }

    public void setCebxCryptExe(String cebxCryptExe) {
        this.cebxCryptExe = cebxCryptExe;
    }

    public static String getCebxMaker() {
        return cebxMaker;
    }

    public void setCebxMaker(String cebxMaker) {
        this.cebxMaker = cebxMaker;
    }

    public String getCopyRightExtractExe() {
        return copyRightExtractExe;
    }

    public void setCopyRightExtractExe(String copyRightExtractExe) {
        this.copyRightExtractExe = copyRightExtractExe;
    }

    public String getTargetCopyRightDir() {
        return targetCopyRightDir;
    }

    public void setTargetCopyRightDir(String targetCopyRightDir) {
        this.targetCopyRightDir = targetCopyRightDir;
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

    public String getUpAuthFile() {
        return upAuthFile;
    }

    public void setUpAuthFile(String upAuthFile) {
        this.upAuthFile = upAuthFile;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
