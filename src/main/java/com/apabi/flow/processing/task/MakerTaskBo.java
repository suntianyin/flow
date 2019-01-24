package com.apabi.flow.processing.task;

import java.util.Map;

/**
 * @author guanpp
 * @date 2019/1/24 11:36
 * @description
 */
public class MakerTaskBo {

    private String batchId;

    private String dirPath;

    private Map<String, String[]> fileMap;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public Map<String, String[]> getFileMap() {
        return fileMap;
    }

    public void setFileMap(Map<String, String[]> fileMap) {
        this.fileMap = fileMap;
    }
}
