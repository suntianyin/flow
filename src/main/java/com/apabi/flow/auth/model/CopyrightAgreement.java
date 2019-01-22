package com.apabi.flow.auth.model;

import com.apabi.flow.auth.constant.AgreementTypeEnum;
import com.apabi.flow.auth.constant.AuthTypeEnum;

import java.util.Date;

public class CopyrightAgreement {
    private String caid;

    private String agreementNum;

    private String agreementTitle;

    private String copyrightOwner;

    private String copyrightOwnerId;

    private String agreementFileName;

    private String agreementFilePath;

    private Date startDate;

    private Date endDate;
    //0-仅2B 1-2B+云联盟 2-2B2C 3-无
    private AuthTypeEnum authType;

    private Double assignPercent;
    //分成比例：0-定价分成，1-售价分成，2-买断，3-无
    private int assignRule;

    private int isinternetCommunication;

    private int isCopy;

    private int isPublishing;

    private int isHireRight;

    private int isExhibitionRight;

    private int isPerformanceRight;

    private int isScreeningRight;

    private int isBroadcastingRight;

    private int isAdaptationRight;

    private int isTranslationRight;

    private int isEditRight;

    private int isTransfeRright;

    private int isContentworkRight;

    private String isOtherRight;

    private int isAgentmaintainlegalRight;

    private int isOnlyOwner;

    private int isHasAuthorRight;

    private int isAutoPostpone;

    private int yearNum;

    private String contentManagerName;

    private Date signDate;

    private Date obtainDate;

    private int status;

    private String operator;

    private Date operatedate;

    //协议类型
    private AgreementTypeEnum agreementType;

    public AgreementTypeEnum getAgreementType() {
        return agreementType;
    }

    public void setAgreementType(AgreementTypeEnum agreementType) {
        this.agreementType = agreementType;
    }

    public String getCaid() {
        return caid;
    }

    public void setCaid(String caid) {
        this.caid = caid;
    }

    public String getAgreementNum() {
        return agreementNum;
    }

    public void setAgreementNum(String agreementNum) {
        this.agreementNum = agreementNum;
    }

    public String getAgreementTitle() {
        return agreementTitle;
    }

    public void setAgreementTitle(String agreementTitle) {
        this.agreementTitle = agreementTitle;
    }

    public String getCopyrightOwner() {
        return copyrightOwner;
    }

    public void setCopyrightOwner(String copyrightOwner) {
        this.copyrightOwner = copyrightOwner;
    }

    public String getAgreementFileName() {
        return agreementFileName;
    }

    public void setAgreementFileName(String agreementFileName) {
        this.agreementFileName = agreementFileName;
    }

    public String getAgreementFilePath() {
        return agreementFilePath;
    }

    public void setAgreementFilePath(String agreementFilePath) {
        this.agreementFilePath = agreementFilePath;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public AuthTypeEnum getAuthType() {
        return authType;
    }

    public void setAuthType(AuthTypeEnum authType) {
        this.authType = authType;
    }

    public Double getAssignPercent() {
        return assignPercent;
    }

    public void setAssignPercent(Double assignPercent) {
        this.assignPercent = assignPercent;
    }

    public int getAssignRule() {
        return assignRule;
    }

    public void setAssignRule(int assignRule) {
        this.assignRule = assignRule;
    }

    public int getIsinternetCommunication() {
        return isinternetCommunication;
    }

    public void setIsinternetCommunication(int isinternetCommunication) {
        this.isinternetCommunication = isinternetCommunication;
    }

    public int getIsCopy() {
        return isCopy;
    }

    public void setIsCopy(int isCopy) {
        this.isCopy = isCopy;
    }

    public int getIsPublishing() {
        return isPublishing;
    }

    public void setIsPublishing(int isPublishing) {
        this.isPublishing = isPublishing;
    }

    public int getIsHireRight() {
        return isHireRight;
    }

    public void setIsHireRight(int isHireRight) {
        this.isHireRight = isHireRight;
    }

    public int getIsExhibitionRight() {
        return isExhibitionRight;
    }

    public void setIsExhibitionRight(int isExhibitionRight) {
        this.isExhibitionRight = isExhibitionRight;
    }

    public int getIsPerformanceRight() {
        return isPerformanceRight;
    }

    public void setIsPerformanceRight(int isPerformanceRight) {
        this.isPerformanceRight = isPerformanceRight;
    }

    public int getIsScreeningRight() {
        return isScreeningRight;
    }

    public void setIsScreeningRight(int isScreeningRight) {
        this.isScreeningRight = isScreeningRight;
    }

    public int getIsBroadcastingRight() {
        return isBroadcastingRight;
    }

    public void setIsBroadcastingRight(int isBroadcastingRight) {
        this.isBroadcastingRight = isBroadcastingRight;
    }

    public int getIsAdaptationRight() {
        return isAdaptationRight;
    }

    public void setIsAdaptationRight(int isAdaptationRight) {
        this.isAdaptationRight = isAdaptationRight;
    }

    public int getIsTranslationRight() {
        return isTranslationRight;
    }

    public void setIsTranslationRight(int isTranslationRight) {
        this.isTranslationRight = isTranslationRight;
    }

    public int getIsEditRight() {
        return isEditRight;
    }

    public void setIsEditRight(int isEditRight) {
        this.isEditRight = isEditRight;
    }

    public int getIsTransfeRright() {
        return isTransfeRright;
    }

    public void setIsTransfeRright(int isTransfeRright) {
        this.isTransfeRright = isTransfeRright;
    }

    public int getIsContentworkRight() {
        return isContentworkRight;
    }

    public void setIsContentworkRight(int isContentworkRight) {
        this.isContentworkRight = isContentworkRight;
    }

    public String getIsOtherRight() {
        return isOtherRight;
    }

    public void setIsOtherRight(String isOtherRight) {
        this.isOtherRight = isOtherRight;
    }

    public int getIsAgentmaintainlegalRight() {
        return isAgentmaintainlegalRight;
    }

    public void setIsAgentmaintainlegalRight(int isAgentmaintainlegalRight) {
        this.isAgentmaintainlegalRight = isAgentmaintainlegalRight;
    }

    public int getIsOnlyOwner() {
        return isOnlyOwner;
    }

    public void setIsOnlyOwner(int isOnlyOwner) {
        this.isOnlyOwner = isOnlyOwner;
    }

    public int getIsHasAuthorRight() {
        return isHasAuthorRight;
    }

    public void setIsHasAuthorRight(int isHasAuthorRight) {
        this.isHasAuthorRight = isHasAuthorRight;
    }

    public int getIsAutoPostpone() {
        return isAutoPostpone;
    }

    public void setIsAutoPostpone(int isAutoPostpone) {
        this.isAutoPostpone = isAutoPostpone;
    }

    public int getYearNum() {
        return yearNum;
    }

    public void setYearNum(int yearNum) {
        this.yearNum = yearNum;
    }

    public String getContentManagerName() {
        return contentManagerName;
    }

    public void setContentManagerName(String contentManagerName) {
        this.contentManagerName = contentManagerName;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public Date getObtainDate() {
        return obtainDate;
    }

    public void setObtainDate(Date obtainDate) {
        this.obtainDate = obtainDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOperatedate() {
        return operatedate;
    }

    public void setOperatedate(Date operatedate) {
        this.operatedate = operatedate;
    }

    public String getCopyrightOwnerId() {
        return copyrightOwnerId;
    }

    public void setCopyrightOwnerId(String copyrightOwnerId) {
        this.copyrightOwnerId = copyrightOwnerId;
    }
}