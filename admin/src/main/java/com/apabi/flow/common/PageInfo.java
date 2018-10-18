package com.apabi.flow.common;

import com.github.pagehelper.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageInfo<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    //当前页
    private Integer page;
    //每页的数量
    private Integer pageSize = 25;
    //总记录数
    private Long records;
    //总页数
    private Integer total;
    //结果集
//    private List<T> list;
    private List<T> rows;
    //是否为第一页
    private boolean isFirstPage = false;
    //是否为最后一页
    private boolean isLastPage = false;

    private Map<String,Object> filters = new HashMap<>();

    public PageInfo() {
    }

    /**
     * 包装Page对象
     *
     * @param list
     */
    public PageInfo(List<T> list) {
        if (list instanceof Page) {
            Page page = (Page) list;
            this.page = page.getPageNum();
            this.pageSize = page.getPageSize();

            this.total = page.getPages();
            this.rows = page;
            this.records = page.getTotal();
        } else if (list instanceof Collection) {
            this.page = 1;
            this.pageSize = list.size();

            this.total = 1;
            this.rows = list;
            this.records =  (long)list.size();
        }
        if (list instanceof Collection) {
            //判断页面边界
            judgePageBoudary();
        }
    }

    /**
     * 判定页面边界
     */
    private void judgePageBoudary() {
        isFirstPage = page == 1;
        isLastPage = page == total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    public Long getRecords() {
        return records;
    }

    public void setRecords(Long records) {
        this.records = records;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public void setIsFirstPage(boolean isFirstPage) {
        this.isFirstPage = isFirstPage;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", records=" + records +
                ", total=" + total +
                ", rows=" + rows +
                ", isFirstPage=" + isFirstPage +
                ", isLastPage=" + isLastPage +
                '}';
    }
}
