package com.apabi.flow.book.model;

/**
 * @author guanpp
 * @date 2018/9/26 17:52
 * @description
 */
public class BookBatchRes {

    private String fileName;

    private String metaId;

    private Integer status;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return fileName + "," + metaId + "," + status + ";";
    }
}
