package com.apabi.flow.book.model;

/**
 * @author guanpp
 * @date 2018/9/18 11:27
 * @description
 */
public class BookMetaBatch {

    private String fileName;

    private String fileIsbn;
    
    private String metaId;

    private String title;

    private String isbn;

    private String isbn13;

    private String creator;

    private String publisher;

    private Integer hasFlow;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileIsbn() {
        return fileIsbn;
    }

    public void setFileIsbn(String fileIsbn) {
        this.fileIsbn = fileIsbn;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getHasFlow() {
        return hasFlow;
    }

    public void setHasFlow(Integer hasFlow) {
        this.hasFlow = hasFlow;
    }
}
