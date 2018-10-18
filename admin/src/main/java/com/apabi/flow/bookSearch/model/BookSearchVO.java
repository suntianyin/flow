package com.apabi.flow.bookSearch.model;

/**
 * @Author pipi
 * @Date 2018/9/10 12:18
 **/
public class BookSearchVO {
    private String metaId;
    private String title;
    private String creator;
    private String publisher;
    private String issuedDate;
    private String isbn;
    private String isbn10;
    private String isbn13;
    private String hasFlow;
    private String hasCebx;
    private String isPublicCopyRight;

    public BookSearchVO() {
    }

    public BookSearchVO(String metaId, String title, String creator, String publisher, String issuedDate, String isbn, String isbn10, String isbn13, String hasFlow, String hasCebx, String isPublicCopyRight) {
        this.metaId = metaId;
        this.title = title;
        this.creator = creator;
        this.publisher = publisher;
        this.issuedDate = issuedDate;
        this.isbn = isbn;
        this.isbn10 = isbn10;
        this.isbn13 = isbn13;
        this.hasFlow = hasFlow;
        this.hasCebx = hasCebx;
        this.isPublicCopyRight = isPublicCopyRight;
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

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
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

    public String getHasFlow() {
        return hasFlow;
    }

    public void setHasFlow(String hasFlow) {
        this.hasFlow = hasFlow;
    }

    public String getHasCebx() {
        return hasCebx;
    }

    public void setHasCebx(String hasCebx) {
        this.hasCebx = hasCebx;
    }

    public String getIsPublicCopyRight() {
        return isPublicCopyRight;
    }

    public void setIsPublicCopyRight(String isPublicCopyRight) {
        this.isPublicCopyRight = isPublicCopyRight;
    }

    @Override
    public String toString() {
        return "BookSearchVO{" +
                "metaId='" + metaId + '\'' +
                ", title='" + title + '\'' +
                ", creator='" + creator + '\'' +
                ", publisher='" + publisher + '\'' +
                ", issuedDate='" + issuedDate + '\'' +
                ", isbn='" + isbn + '\'' +
                ", isbn10='" + isbn10 + '\'' +
                ", isbn13='" + isbn13 + '\'' +
                ", hasFlow='" + hasFlow + '\'' +
                ", hasCebx='" + hasCebx + '\'' +
                ", isPublicCopyRight='" + isPublicCopyRight + '\'' +
                '}';
    }
}
