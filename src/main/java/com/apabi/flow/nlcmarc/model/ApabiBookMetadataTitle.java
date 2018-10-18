package com.apabi.flow.nlcmarc.model;

import java.util.Date;

/**
 * @Author pipi
 * @Date 2018/10/10 11:03
 **/
public class ApabiBookMetadataTitle {
    private String id;

    private String metaId;

    private String nlcMarcIdentifier;

    private String title;

    private String subTitle;

    private String titlePinyin;

    private String seriesTitle;

    private String parallelSeriesTitle;

    private String seriesSubTitle;

    private String volumeTitle;

    private String parallelTitle;

    private String coverTitle;

    private String addedPageTitle;

    private String captionTitle;

    private String runningTitle;

    private String spineTitle;

    private String otherVariantTitle;

    private String operator;

    private Date createTime;

    private Date updateTime;

    private String volume200;

    private String volume200Title;

    private String volume;

    private String volume500;

    private String volume500Title;

    private String uniformTitle;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public String getNlcMarcIdentifier() {
        return nlcMarcIdentifier;
    }

    public void setNlcMarcIdentifier(String nlcMarcIdentifier) {
        this.nlcMarcIdentifier = nlcMarcIdentifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getTitlePinyin() {
        return titlePinyin;
    }

    public void setTitlePinyin(String titlePinyin) {
        this.titlePinyin = titlePinyin;
    }

    public String getSeriesTitle() {
        return seriesTitle;
    }

    public void setSeriesTitle(String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }

    public String getParallelSeriesTitle() {
        return parallelSeriesTitle;
    }

    public void setParallelSeriesTitle(String parallelSeriesTitle) {
        this.parallelSeriesTitle = parallelSeriesTitle;
    }

    public String getSeriesSubTitle() {
        return seriesSubTitle;
    }

    public void setSeriesSubTitle(String seriesSubTitle) {
        this.seriesSubTitle = seriesSubTitle;
    }

    public String getVolumeTitle() {
        return volumeTitle;
    }

    public void setVolumeTitle(String volumeTitle) {
        this.volumeTitle = volumeTitle;
    }

    public String getParallelTitle() {
        return parallelTitle;
    }

    public void setParallelTitle(String parallelTitle) {
        this.parallelTitle = parallelTitle;
    }

    public String getCoverTitle() {
        return coverTitle;
    }

    public void setCoverTitle(String coverTitle) {
        this.coverTitle = coverTitle;
    }

    public String getAddedPageTitle() {
        return addedPageTitle;
    }

    public void setAddedPageTitle(String addedPageTitle) {
        this.addedPageTitle = addedPageTitle;
    }

    public String getCaptionTitle() {
        return captionTitle;
    }

    public void setCaptionTitle(String captionTitle) {
        this.captionTitle = captionTitle;
    }

    public String getRunningTitle() {
        return runningTitle;
    }

    public void setRunningTitle(String runningTitle) {
        this.runningTitle = runningTitle;
    }

    public String getSpineTitle() {
        return spineTitle;
    }

    public void setSpineTitle(String spineTitle) {
        this.spineTitle = spineTitle;
    }

    public String getOtherVariantTitle() {
        return otherVariantTitle;
    }

    public void setOtherVariantTitle(String otherVariantTitle) {
        this.otherVariantTitle = otherVariantTitle;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
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

    public String getVolume200() {
        return volume200;
    }

    public void setVolume200(String volume200) {
        this.volume200 = volume200;
    }

    public String getVolume200Title() {
        return volume200Title;
    }

    public void setVolume200Title(String volume200Title) {
        this.volume200Title = volume200Title;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getVolume500() {
        return volume500;
    }

    public void setVolume500(String volume500) {
        this.volume500 = volume500;
    }

    public String getVolume500Title() {
        return volume500Title;
    }

    public void setVolume500Title(String volume500Title) {
        this.volume500Title = volume500Title;
    }

    public String getUniformTitle() {
        return uniformTitle;
    }

    public void setUniformTitle(String uniformTitle) {
        this.uniformTitle = uniformTitle;
    }

    @Override
    public String toString() {
        return "ApabiBookMetadataTitle{" +
                "id='" + id + '\'' +
                ", metaId='" + metaId + '\'' +
                ", nlcMarcIdentifier='" + nlcMarcIdentifier + '\'' +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", titlePinyin='" + titlePinyin + '\'' +
                ", seriesTitle='" + seriesTitle + '\'' +
                ", parallelSeriesTitle='" + parallelSeriesTitle + '\'' +
                ", seriesSubTitle='" + seriesSubTitle + '\'' +
                ", volumeTitle='" + volumeTitle + '\'' +
                ", parallelTitle='" + parallelTitle + '\'' +
                ", coverTitle='" + coverTitle + '\'' +
                ", addedPageTitle='" + addedPageTitle + '\'' +
                ", captionTitle='" + captionTitle + '\'' +
                ", runningTitle='" + runningTitle + '\'' +
                ", spineTitle='" + spineTitle + '\'' +
                ", otherVariantTitle='" + otherVariantTitle + '\'' +
                ", operator='" + operator + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", volume200='" + volume200 + '\'' +
                ", volume200Title='" + volume200Title + '\'' +
                ", volume='" + volume + '\'' +
                ", volume500='" + volume500 + '\'' +
                ", volume500Title='" + volume500Title + '\'' +
                ", uniformTitle='" + uniformTitle + '\'' +
                '}';
    }
}
