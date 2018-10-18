package com.apabi.flow.publish.model;

/**
 * @Author pipi
 * @Date 2018/8/28 10:19
 **/
public class PageEntity<T> {
    // 状态码
    private Integer code = 200;
    // 提示消息
    private String message = "";
    // 请求的唯一标识
    private String apiId = "";
    // 记录总数
    private Integer totalCount = 0;

    public PageEntity() {
    }

    public PageEntity(Integer code, String message, String apiId, Integer totalCount) {
        this.code = code;
        this.message = message;
        this.apiId = apiId;
        this.totalCount = totalCount;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "PageEntity{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", apiId='" + apiId + '\'' +
                ", totalCount=" + totalCount +
                '}';
    }
}
