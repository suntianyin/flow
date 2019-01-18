package com.apabi.flow.processing.model;

import com.apabi.flow.processing.constant.BatchStateEnum;
import com.apabi.flow.processing.constant.DeleteFlagEnum;
import com.apabi.flow.processing.constant.SourceTypeEnum;

import java.util.Date;

/**
 * @author supeng
 */
public class Batch {
    private String id;

    private String manager;

    private String batchId;

    private String outUnit;

    private SourceTypeEnum sourceType;

    private String copyrightOwner;

    private String documentNum;

    private BatchStateEnum batchState;

    private String creator;

    private String auditor;

    private String checker;

    private String memo;

    private Date auditTime;

    private Date checkTime;

    private Date createTime;

    private Date updateTime;

    private DeleteFlagEnum deleteFlag;

    private String documentFormat;//文档格式

    private Date distributionOutTime;//分配外协

    private Date submitTime;//书单提交时间

    private Date productionSchedulingTime;//排产时间

    private String resourcePath;

    private Integer convertStatus = 0;

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public Date getDistributionOutTime() {
        return distributionOutTime;
    }

    public void setDistributionOutTime(Date distributionOutTime) {
        this.distributionOutTime = distributionOutTime;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Date getProductionSchedulingTime() {
        return productionSchedulingTime;
    }

    public void setProductionSchedulingTime(Date productionSchedulingTime) {
        this.productionSchedulingTime = productionSchedulingTime;
    }

    public void setCopyrightOwner(String copyrightOwner) {
        this.copyrightOwner = copyrightOwner;
    }

    public String getDocumentFormat() {
        return documentFormat;
    }

    public void setDocumentFormat(String documentFormat) {
        this.documentFormat = documentFormat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager == null ? null : manager.trim();
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId == null ? null : batchId.trim();
    }

    public String getOutUnit() {
        return outUnit;
    }

    public void setOutUnit(String outUnit) {
        this.outUnit = outUnit == null ? null : outUnit.trim();
    }

    public SourceTypeEnum getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceTypeEnum sourceType) {
        this.sourceType = sourceType;
    }

    public String getCopyrightOwner() {
        return copyrightOwner;
    }

    public void setCopyrigntOwner(String copyrightOwner) {
        this.copyrightOwner = copyrightOwner == null ? null : copyrightOwner.trim();
    }

    public String getDocumentNum() {
        return documentNum;
    }

    public void setDocumentNum(String documentNum) {
        this.documentNum = documentNum == null ? null : documentNum.trim();
    }

    public BatchStateEnum getBatchState() {
        return batchState;
    }

    public void setBatchState(BatchStateEnum batchState) {
        this.batchState = batchState;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor == null ? null : auditor.trim();
    }

    public String getChecker() {
        return checker;
    }

    public void setChecker(String checker) {
        this.checker = checker == null ? null : checker.trim();
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public DeleteFlagEnum getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(DeleteFlagEnum deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Integer getConvertStatus() {
        return convertStatus;
    }

    public void setConvertStatus(Integer convertStatus) {
        this.convertStatus = convertStatus;
    }
}