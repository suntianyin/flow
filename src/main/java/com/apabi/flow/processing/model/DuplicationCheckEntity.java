package com.apabi.flow.processing.model;

import com.apabi.flow.book.model.BookMeta;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

/**
 * 功能描述： <br>
 * <云加工模块使用到的 BookMeta 实体>
 *
 * @author supeng
 * @date 2018/9/17 17:15
 * @since 1.0.0
 */
public class DuplicationCheckEntity {

    /**
     * 对应书目信息
     */
    private Bibliotheca bibliotheca;

    /**
     * 对应元数据信息
     */
    private BookMeta bookMeta;
    /**
     * 检测批次信息
     */
    private Bibliotheca bibliotheca2;

    public Bibliotheca getBibliotheca2() {
        return bibliotheca2;
    }

    public void setBibliotheca2(Bibliotheca bibliotheca2) {
        this.bibliotheca2 = bibliotheca2;
    }

    /**
     * 是否重复标识
     */
    private Integer flag;

    public Integer getRateFlag() {
        return rateFlag;
    }

    public void setRateFlag(Integer rateFlag) {
        this.rateFlag = rateFlag;
    }

    /**
     * 重复率标识
     */
    private Integer rateFlag;

    public Bibliotheca getBibliotheca() {
        return bibliotheca;
    }

    public void setBibliotheca(Bibliotheca bibliotheca) {
        this.bibliotheca = bibliotheca;
    }

    public BookMeta getBookMeta() {
        return bookMeta;
    }

    public void setBookMeta(BookMeta bookMeta) {
        this.bookMeta = bookMeta;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    /**
     * 是否重复标识
     */
    public interface CheckFlag{
        /**
         * 1 = 重复，有 cebx
         */
        int DUPLICATE_YES = 1;

        /**
         * 2 = 重复，无 cebx
         */
        int DUPLICATE_NO = 2;

        /**
         * 3 = 无匹配数据
         */
        int EMPTY = 3;
    }

    /**
     * 重复率标识
     */
    public interface RateFlag{
        /**
         * 重复率 大于等于 标识
         */
        int DUPLICATE_RATE_GT_EQ_FLAG = 1;

        /**
         * 重复率 小于 标识
         */
        int DUPLICATE_RATE_LT_FLAG = 2;

        /**
         * 没有匹配数据
         */
        int NO_DATA = 3;

        /**
         * 重复率
         */
        double RATE = 0.95d;
    }
}
