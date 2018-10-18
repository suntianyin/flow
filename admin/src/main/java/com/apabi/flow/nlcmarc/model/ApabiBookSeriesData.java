package com.apabi.flow.nlcmarc.model;

import java.util.Date;

/**
 * @Author pipi
 * @Date 2018/10/10 17:00
 **/
public class ApabiBookSeriesData {
    private String id;

    private String nlcMarcIdentifier;

    private String isbn;

    private String metaId;

    private String seriesId;

    private String title;

    private String author;

    private String seriesTitle;

    private String parallelSeriesTitle;

    private String seriesSubTitle;

    private String seriesAuthor;

    private String volume;

    private String volumeTitle;

    private String volumeId;

    private Date createTime;

    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNlcMarcIdentifier() {
        return nlcMarcIdentifier;
    }

    public void setNlcMarcIdentifier(String nlcMarcIdentifier) {
        this.nlcMarcIdentifier = nlcMarcIdentifier;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public String getRelationId() {
        return seriesId;
    }

    public void setRelationId(String seriesId) {
        this.seriesId = seriesId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getSeriesAuthor() {
        return seriesAuthor;
    }

    public void setSeriesAuthor(String seriesAuthor) {
        this.seriesAuthor = seriesAuthor;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getVolumeTitle() {
        return volumeTitle;
    }

    public void setVolumeTitle(String volumeTitle) {
        this.volumeTitle = volumeTitle;
    }

    public String getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
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

    @Override
    public String toString() {
        return "ApabiBookSeriesData{" +
                "id='" + id + '\'' +
                ", nlcMarcIdentifier='" + nlcMarcIdentifier + '\'' +
                ", isbn='" + isbn + '\'' +
                ", metaId='" + metaId + '\'' +
                ", seriesId='" + seriesId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", seriesTitle='" + seriesTitle + '\'' +
                ", parallelSeriesTitle='" + parallelSeriesTitle + '\'' +
                ", seriesSubTitle='" + seriesSubTitle + '\'' +
                ", seriesAuthor='" + seriesAuthor + '\'' +
                ", volume='" + volume + '\'' +
                ", volumeTitle='" + volumeTitle + '\'' +
                ", volumeId='" + volumeId + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}