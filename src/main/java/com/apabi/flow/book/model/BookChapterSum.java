package com.apabi.flow.book.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author guanpp
 * @date 2018/8/8 15:45
 * @description
 */
@Table(name="APABI_BOOK_CHAPTER")
@Entity
public class BookChapterSum {
    @Id
    @Column(name = "COMID")
    private String comId;

    @Column(name = "CHAPTERNUM")
    private int chapterNum;

    @Column(name = "SHARDSUM")
    private int shardSum;

    @Column(name = "WORDSUM")
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
