package com.apabi.flow.common;

/**
 * @author guanpp
 * @date 2018/8/7 9:35
 * @description
 */
public class ResultEntity {

    private int status;

    private String msg;

    private Object body;

    public ResultEntity() {
    }

    public ResultEntity(int status, String msg, Object body) {
        this.status = status;
        this.msg = msg;
        this.body = body;
    }

    public ResultEntity(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
