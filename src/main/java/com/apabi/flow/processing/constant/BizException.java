package com.apabi.flow.processing.constant;

/**
 * 功能描述： <br>
 * <业务异常>
 *
 * @author supeng
 * @date 2018/9/26 13:49
 * @since 1.0.0
 */
public class BizException extends Exception {

    public BizException() {
        super();
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    protected BizException(String message, Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
