package com.apabi.flow.common.model;

import com.apabi.flow.book.model.BookCataRows;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guanpp
 * @date 2018/10/26 16:24
 * @description
 */
public class ZtreeNode {

    private String name;

    private int nodeId;

    private String src;

    private int wordSum;

    private int ebookPageNum;

    private boolean open = true;

    private List<ZtreeNode> children = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
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

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public List<ZtreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<ZtreeNode> children) {
        this.children = children;
    }
}
