package com.apabi.flow.book.model;

/**
 * @author: sunty
 * @date: 2018/11/30 10:01
 * @description:
 */

public class BookMetaFromExcel {
    private BookMeta bookMeta;

    private BookMeta bookMetaTemp;

    private Integer state;//0代表不重复，1代表重复

    public BookMeta getBookMeta() {
        return bookMeta;
    }

    public void setBookMeta(BookMeta bookMeta) {
        this.bookMeta = bookMeta;
    }

    public BookMeta getBookMetaTemp() {
        return bookMetaTemp;
    }

    public void setBookMetaTemp(BookMeta bookMetaTemp) {
        this.bookMetaTemp = bookMetaTemp;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public BookMetaFromExcel(BookMeta bookMeta, BookMeta bookMetaTemp, Integer state) {
        this.bookMeta = bookMeta;
        this.bookMetaTemp = bookMetaTemp;
        this.state = state;
    }

    @Override
    public String toString() {
        return "BookMetaFromExcel{" +
                "bookMeta=" + bookMeta +
                ", bookMetaTemp=" + bookMetaTemp +
                ", state=" + state +
                '}';
    }
}
