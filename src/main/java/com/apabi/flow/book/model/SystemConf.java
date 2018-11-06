package com.apabi.flow.book.model;

import java.util.Date;

public class SystemConf {
    private String id;

    private String confname;

    private String confkey;

    private String confvalue;

    private String description;

    private String operator;

    private Date createtime;

    private Date updatetime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getConfname() {
        return confname;
    }

    public void setConfname(String confname) {
        this.confname = confname == null ? null : confname.trim();
    }

    public String getConfkey() {
        return confkey;
    }

    public void setConfkey(String confkey) {
        this.confkey = confkey == null ? null : confkey.trim();
    }

    public String getConfvalue() {
        return confvalue;
    }

    public void setConfvalue(String confvalue) {
        this.confvalue = confvalue == null ? null : confvalue.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}