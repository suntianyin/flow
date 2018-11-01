package com.apabi.flow.common.api;

/**
 * 功能描述： <br>
 * <通过code 值 转换成枚举值>
 *
 * @author supeng
 * @date 2018/9/5 17:53
 * @since 1.0.0
 */
public interface CodeBaseEnum<T> {
    T getCode();
}
