package com.apabi.flow.author.model;

import com.apabi.flow.author.constant.AuthorTypeEnum;
import com.apabi.flow.author.constant.DieOver50Enum;
import com.apabi.flow.author.constant.SexEnum;
import com.apabi.flow.author.constant.TitleTypeEnum;

import java.util.Date;

/**
 * 作者信息实体类
 * @author supeng
 */
public class Author {
    private String id;

    private String nationalityCode;

    private String birthday;

    private String careerClassCode;

    private Date createTime;

    private String deathDay;

    private DieOver50Enum dieOver50;

    private String dynastyName;

    private String endDate;

    private String headPortraitPath;

    private String nationalCode;

    private String nlcAuthorId;

    private String operator;

    private String originCode;

    private String personId;

    private String qualificationCode;

    private String serviceAgency;

    private SexEnum sexCode;

    private String startDate;

    private String summary;

    private String title;

    private TitleTypeEnum titleType;

    private AuthorTypeEnum type;

    private Date updateTime;

    public Author() {
    }

    public Author(String id, String nationalityCode, String birthday, String careerClassCode, Date createTime, String deathDay, DieOver50Enum dieOver50, String dynastyName, String endDate, String headPortraitPath, String nationalCode, String nlcAuthorId, String operator, String originCode, String personId, String qualificationCode, String serviceAgency, SexEnum sexCode, String startDate, String summary, String title, TitleTypeEnum titleType, AuthorTypeEnum type, Date updateTime) {
        this.id = id;
        this.nationalityCode = nationalityCode;
        this.birthday = birthday;
        this.careerClassCode = careerClassCode;
        this.createTime = createTime;
        this.deathDay = deathDay;
        this.dieOver50 = dieOver50;
        this.dynastyName = dynastyName;
        this.endDate = endDate;
        this.headPortraitPath = headPortraitPath;
        this.nationalCode = nationalCode;
        this.nlcAuthorId = nlcAuthorId;
        this.operator = operator;
        this.originCode = originCode;
        this.personId = personId;
        this.qualificationCode = qualificationCode;
        this.serviceAgency = serviceAgency;
        this.sexCode = sexCode;
        this.startDate = startDate;
        this.summary = summary;
        this.title = title;
        this.titleType = titleType;
        this.type = type;
        this.updateTime = updateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getNationalityCode() {
        return nationalityCode;
    }

    public void setNationalityCode(String nationalityCode) {
        this.nationalityCode = nationalityCode == null ? null : nationalityCode.trim();
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday == null ? null : birthday.trim();
    }

    public String getCareerClassCode() {
        return careerClassCode;
    }

    public void setCareerClassCode(String careerClassCode) {
        this.careerClassCode = careerClassCode == null ? null : careerClassCode.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDeathDay() {
        return deathDay;
    }

    public void setDeathDay(String deathDay) {
        this.deathDay = deathDay == null ? null : deathDay.trim();
    }

    public DieOver50Enum getDieOver50() {
        return dieOver50;
    }

    public void setDieOver50(DieOver50Enum dieOver50) {
        this.dieOver50 = dieOver50;
    }

    public String getDynastyName() {
        return dynastyName;
    }

    public void setDynastyName(String dynastyName) {
        this.dynastyName = dynastyName == null ? null : dynastyName.trim();
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate == null ? null : endDate.trim();
    }

    public String getHeadPortraitPath() {
        return headPortraitPath;
    }

    public void setHeadPortraitPath(String headPortraitPath) {
        this.headPortraitPath = headPortraitPath == null ? null : headPortraitPath.trim();
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode == null ? null : nationalCode.trim();
    }

    public String getNlcAuthorId() {
        return nlcAuthorId;
    }

    public void setNlcAuthorId(String nlcAuthorId) {
        this.nlcAuthorId = nlcAuthorId == null ? null : nlcAuthorId.trim();
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    public String getOriginCode() {
        return originCode;
    }

    public void setOriginCode(String originCode) {
        this.originCode = originCode == null ? null : originCode.trim();
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId == null ? null : personId.trim();
    }

    public String getQualificationCode() {
        return qualificationCode;
    }

    public void setQualificationCode(String qualificationCode) {
        this.qualificationCode = qualificationCode == null ? null : qualificationCode.trim();
    }

    public String getServiceAgency() {
        return serviceAgency;
    }

    public void setServiceAgency(String serviceAgency) {
        this.serviceAgency = serviceAgency == null ? null : serviceAgency.trim();
    }

    public SexEnum getSexCode() {
        return sexCode;
    }

    public void setSexCode(SexEnum sexCode) {
        this.sexCode = sexCode;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate == null ? null : startDate.trim();
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary == null ? null : summary.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public TitleTypeEnum getTitleType() {
        return titleType;
    }

    public void setTitleType(TitleTypeEnum titleType) {
        this.titleType = titleType;
    }

    public AuthorTypeEnum getType() {
        return type;
    }

    public void setType(AuthorTypeEnum type) {
        this.type = type;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id='" + id + '\'' +
                ", nationalityCode='" + nationalityCode + '\'' +
                ", birthday='" + birthday + '\'' +
                ", careerClassCode='" + careerClassCode + '\'' +
                ", createTime=" + createTime +
                ", deathDay='" + deathDay + '\'' +
                ", dieOver50=" + dieOver50 +
                ", dynastyName='" + dynastyName + '\'' +
                ", endDate='" + endDate + '\'' +
                ", headPortraitPath='" + headPortraitPath + '\'' +
                ", nationalCode='" + nationalCode + '\'' +
                ", nlcAuthorId='" + nlcAuthorId + '\'' +
                ", operator='" + operator + '\'' +
                ", originCode='" + originCode + '\'' +
                ", personId='" + personId + '\'' +
                ", qualificationCode='" + qualificationCode + '\'' +
                ", serviceAgency='" + serviceAgency + '\'' +
                ", sexCode=" + sexCode +
                ", startDate='" + startDate + '\'' +
                ", summary='" + summary + '\'' +
                ", title='" + title + '\'' +
                ", titleType=" + titleType +
                ", type=" + type +
                ", updateTime=" + updateTime +
                '}';
    }
}