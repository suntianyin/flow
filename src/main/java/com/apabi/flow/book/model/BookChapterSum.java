package com.apabi.flow.book.model;

/**
 * @author guanpp
 * @date 2018/8/8 15:45
 * @description
 */
public class BookChapterSum {
    private String comId;

    private int chapterNum;

    private int shardSum;

    private int wordSum;

    public String getComId() {
        return comId;
    }

    public void setComId(String comId) {
        this.comId = comId;
    }

    public int getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(int chapterNum) {
        this.chapterNum = chapterNum;
    }

    public int getShardSum() {
        return shardSum;
    }

    public void setShardSum(int shardSum) {
        this.shardSum = shardSum;
    }

    public int getWordSum() {
        return wordSum;
    }

    public void setWordSum(int wordSum) {
        this.wordSum = wordSum;
    }
}
