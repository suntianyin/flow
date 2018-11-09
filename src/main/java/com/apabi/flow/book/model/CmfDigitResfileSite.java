package com.apabi.flow.book.model;

/**
 * @author guanpp
 * @date 2018/11/7 10:18
 * @description
 */
public class CmfDigitResfileSite {
    private Integer fileId;

    private Integer siteId;

    private String urlFileName;

    private String urlFilePath;

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getUrlFileName() {
        return urlFileName;
    }

    public void setUrlFileName(String urlFileName) {
        this.urlFileName = urlFileName;
    }

    public String getUrlFilePath() {
        return urlFilePath;
    }

    public void setUrlFilePath(String urlFilePath) {
        this.urlFilePath = urlFilePath;
    }
}
