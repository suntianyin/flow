package com.apabi.flow.auth.model;

import java.util.Date;

//授权协议与授权书单关系基本信息表
public class BookList {
    private String id;
    //协议编号
    private String agreementNum;
    //授权书单编号
    private String bookListNum;
    //书单批次编号
    private String batchNum;
    //资源类型
    private String resType;
    //版权所有者
    private String copyrightOwner;
    //版权所有者id
    private String copyrightOwnerId;
    //文档大概数量
    private Long aboutNum;
    //可加工的数量
    private Long validMakeNum;
    //需授权资源数量
    private Long applyNum;
    //已授权资源数量
    private Long authorizeNum;
    //版权结束时间
    private Date authEndDate;
    //申请授权时间
    private Date submitDate;
    //获得授权时间
    private Date obtainDate;
    //授权书单文件名
    private String fileName;
    //授权书单文件路径
    private String filePath;
    //内容合作经理
    private String coopertor;
    //操作人
    private String opertor;
    //操作时间
    private Date operteDate;

    private String remark;

    private String iscloudCombination;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getAgreementNum() {
        return agreementNum;
    }

    public void setAgreementNum(String agreementNum) {
        this.agreementNum = agreementNum == null ? null : agreementNum.trim();
    }

    public String getBookListNum() {
        return bookListNum;
    }

    public void setBookListNum(String bookListNum) {
        this.bookListNum = bookListNum == null ? null : bookListNum.trim();
    }

    public String getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum == null ? null : batchNum.trim();
    }

    public String getResType() {
        return resType;
    }

    public void setResType(String resType) {
        this.resType = resType == null ? null : resType.trim();
    }

    public String getCopyrightOwner() {
        return copyrightOwner;
    }

    public void setCopyrightOwner(String copyrightOwner) {
        this.copyrightOwner = copyrightOwner == null ? null : copyrightOwner.trim();
    }

    public Long getAboutNum() {
        return aboutNum;
    }

    public void setAboutNum(Long aboutNum) {
        this.aboutNum = aboutNum;
    }

    public Long getValidMakeNum() {
        return validMakeNum;
    }

    public void setValidMakeNum(Long validMakeNum) {
        this.validMakeNum = validMakeNum;
    }

    public Long getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(Long applyNum) {
        this.applyNum = applyNum;
    }

    public Long getAuthorizeNum() {
        return authorizeNum;
    }

    public void setAuthorizeNum(Long authorizeNum) {
        this.authorizeNum = authorizeNum;
    }

    public Date getAuthEndDate() {
        return authEndDate;
    }

    public void setAuthEndDate(Date authEndDate) {
        this.authEndDate = authEndDate;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public Date getObtainDate() {
        return obtainDate;
    }

    public void setObtainDate(Date obtainDate) {
        this.obtainDate = obtainDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath == null ? null : filePath.trim();
    }

    public String getCoopertor() {
        return coopertor;
    }

    public void setCoopertor(String coopertor) {
        this.coopertor = coopertor == null ? null : coopertor.trim();
    }

    public String getOpertor() {
        return opertor;
    }

    public void setOpertor(String opertor) {
        this.opertor = opertor == null ? null : opertor.trim();
    }

    public Date getOperteDate() {
        return operteDate;
    }

    public void setOperteDate(Date operteDate) {
        this.operteDate = operteDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getIscloudCombination() {
        return iscloudCombination;
    }

    public void setIscloudCombination(String iscloudCombination) {
        this.iscloudCombination = iscloudCombination == null ? null : iscloudCombination.trim();
    }

    public String getCopyrightOwnerId() {
        return copyrightOwnerId;
    }

    public void setCopyrightOwnerId(String copyrightOwnerId) {
        this.copyrightOwnerId = copyrightOwnerId;
    }
}