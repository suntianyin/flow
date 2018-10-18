package com.apabi.flow.book.model;

/**
 * @author guanpp
 * @date 2018/8/1 13:51
 * @description
 */
public class BookCataRows {
    private String chapterName;

    private int chapterNum;

    private String url;

    private int wordSum;

    private int ebookPageNum;

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(int chapterNum) {
        this.chapterNum = chapterNum;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWordSum() {
        return wordSum;
    }

    public void setWordSum(int wordSum) {
        this.wordSum = wordSum;
    }

    public int getEbookPageNum() {
        return ebookPageNum;
    }

    public void setEbookPageNum(int ebookPageNum) {
        this.ebookPageNum = ebookPageNum;
    }
}
