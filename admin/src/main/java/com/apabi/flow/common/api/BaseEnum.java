package com.apabi.flow.common.api;

/**
 * 功能描述： <br>
 * <枚举基类，实体中含有枚举类型的值的转换>
 *
 * @author supeng
 * @date 2018/8/27 9:44
 * @since 1.0.0
 */

public interface BaseEnum<E extends Enum<?>, T> {
    /**
     * 返回枚举值
     * @return
     */
    T getCode();
}
