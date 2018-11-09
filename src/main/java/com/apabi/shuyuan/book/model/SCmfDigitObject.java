package com.apabi.shuyuan.book.model;

import javax.sql.DataSource;
import java.util.Date;

/**
 * @author guanpp
 * @date 2018/11/7 10:41
 * @description
 */
public class SCmfDigitObject {

    private Integer fileId;

    private Integer drId;

    private Integer pfileId;

    private String fileName;

    private String fileDesc;

    private String filePath;

    private Integer order;

    private String objId;

    private String pobjId;

    private String doi;

    private Integer fileSize;

    private String format;

    private Integer contentTableInfo;

    private Integer encryptInfo;

    private String objType;

    private Integer imgWidth;

    private Integer imgHeigth;

    private String chapterIndex;

    private Date fileCreatedDate;

    private Date fileLastModDate;

    private Integer contentFormat;

    private Integer cebxSubset;

    private Integer security;

    private String catalog;

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public Integer getDrId() {
        return drId;
    }

    public void setDrId(Integer drId) {
        this.drId = drId;
    }

    public Integer getPfileId() {
        return pfileId;
    }

    public void setPfileId(Integer pfileId) {
        this.pfileId = pfileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDesc() {
        return fileDesc;
    }

    public void setFileDesc(String fileDesc) {
        this.fileDesc = fileDesc;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getPobjId() {
        return pobjId;
    }

    public void setPobjId(String pobjId) {
        this.pobjId = pobjId;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getContentTableInfo() {
        return contentTableInfo;
    }

    public void setContentTableInfo(Integer contentTableInfo) {
        this.contentTableInfo = contentTableInfo;
    }

    public Integer getEncryptInfo() {
        return encryptInfo;
    }

    public void setEncryptInfo(Integer encryptInfo) {
        this.encryptInfo = encryptInfo;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }

    public Integer getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(Integer imgWidth) {
        this.imgWidth = imgWidth;
    }

    public Integer getImgHeigth() {
        return imgHeigth;
    }

    public void setImgHeigth(Integer imgHeigth) {
        this.imgHeigth = imgHeigth;
    }

    public String getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(String chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    public Date getFileCreatedDate() {
        return fileCreatedDate;
    }

    public void setFileCreatedDate(Date fileCreatedDate) {
        this.fileCreatedDate = fileCreatedDate;
    }

    public Date getFileLastModDate() {
        return fileLastModDate;
    }

    public void setFileLastModDate(Date fileLastModDate) {
        this.fileLastModDate = fileLastModDate;
    }

    public Integer getContentFormat() {
        return contentFormat;
    }

    public void setContentFormat(Integer contentFormat) {
        this.contentFormat = contentFormat;
    }

    public Integer getCebxSubset() {
        return cebxSubset;
    }

    public void setCebxSubset(Integer cebxSubset) {
        this.cebxSubset = cebxSubset;
    }

    public Integer getSecurity() {
        return security;
    }

    public void setSecurity(Integer security) {
        this.security = security;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }
}
