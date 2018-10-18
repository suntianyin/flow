package com.apabi.flow.book.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

/**
 * @author guanpp
 * @date 2018/8/24 14:29
 * @description
 */
@Document(collection = "book_metadata")
public class MongoBookMeta {
    @Field("_id")
    private String _id;
    @Field("id")
    private String id;
    @Field("title")
    private String title;
    @Field("creator")
    private String creator;
    @Field("summary")
    private String summary;
    @Field("publisher")
    private String publisher;
    @Field("publishDate")
    private Date publishDate;
    @Field("isbn")
    private String isbn;
    @Field("chapterSum")
    private int chapterSum;
    @Field("wordSum")
    private int wordSum;
    @Field("language")
    private String language;
    @Field("type")
    private String type;
    @Field("cssUrl")
    private String cssUrl;
    @Field("coverUrl")
    private String coverUrl;
    @Field("coverMiniUrl")
    private String coverMiniUrl;
    @Field("bodyClass")
    private String bodyClass;
    @Field("cataRows")
    private List<String> cataRows;
    @Field("optimized")
    private String optimized;
    @Field("original")
    private String original;
    @Field("platform")
    private String platform;
    @Field("_route_")
    private String _route_;
    @Field("libId")
    private List<String> libId;
    @Field("createdDate")
    private Date createdDate;
    @Field("lastModifiedDate")
    private Date lastModifiedDate;
    @Field("fileName")
    private String fileName;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getChapterSum() {
        return chapterSum;
    }

    public void setChapterSum(int chapterSum) {
        this.chapterSum = chapterSum;
    }

    public int getWordSum() {
        return wordSum;
    }

    public void setWordSum(int wordSum) {
        this.wordSum = wordSum;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCssUrl() {
        return cssUrl;
    }

    public void setCssUrl(String cssUrl) {
        this.cssUrl = cssUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getCoverMiniUrl() {
        return coverMiniUrl;
    }

    public void setCoverMiniUrl(String coverMiniUrl) {
        this.coverMiniUrl = coverMiniUrl;
    }

    public String getBodyClass() {
        return bodyClass;
    }

    public void setBodyClass(String bodyClass) {
        this.bodyClass = bodyClass;
    }

    public List<String> getCataRows() {
        return cataRows;
    }

    public void setCataRows(List<String> cataRows) {
        this.cataRows = cataRows;
    }

    public String getOptimized() {
        return optimized;
    }

    public void setOptimized(String optimized) {
        this.optimized = optimized;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String get_route_() {
        return _route_;
    }

    public void set_route_(String _route_) {
        this._route_ = _route_;
    }

    public List<String> getLibId() {
        return libId;
    }

    public void setLibId(List<String> libId) {
        this.libId = libId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
