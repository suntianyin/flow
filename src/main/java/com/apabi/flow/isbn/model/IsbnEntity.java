package com.apabi.flow.isbn.model;

/**
 * @Author pipi
 * @Date 2018/8/23 11:32
 **/
public class IsbnEntity {
    private String isbn;
    private String isbn10;
    private String isbn13;
    private String metaId;
    private String title;
    private String author;
    private String publisher;
    private Integer hasCebx;
    private Integer hasFlow;

    public IsbnEntity() {
    }

    public IsbnEntity(String isbn, String isbn10, String isbn13, String metaId, String title, String author, String publisher, Integer hasCebx, Integer hasFlow) {
        this.isbn = isbn;
        this.isbn10 = isbn10;
        this.isbn13 = isbn13;
        this.metaId = metaId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.hasCebx = hasCebx;
        this.hasFlow = hasFlow;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getHasCebx() {
        return hasCebx;
    }

    public void setHasCebx(Integer hasCebx) {
        this.hasCebx = hasCebx;
    }

    public Integer getHasFlow() {
        return hasFlow;
    }

    public void setHasFlow(Integer hasFlow) {
        this.hasFlow = hasFlow;
    }

    @Override
    public String toString() {
        return "IsbnEntity{" +
                "isbn='" + isbn + '\'' +
                ", isbn10='" + isbn10 + '\'' +
                ", isbn13='" + isbn13 + '\'' +
                ", metaId='" + metaId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", hasCebx=" + hasCebx +
                ", hasFlow=" + hasFlow +
                '}';
    }
}
