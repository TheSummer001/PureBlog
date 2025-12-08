package com.github.toran.common.exception;

import com.github.toran.common.constant.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 *
 * @author toran
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizException extends BaseException {

    private static final long serialVersionUID = 1L;

    public BizException(String message) {
        super(message);
    }

    public BizException(Integer code, String message) {
        super(code, message);
    }

    public BizException(Integer code, String message, Throwable cause) {
        super(code, message, cause);
    }

    /**
     * 使用默认业务错误码创建异常
     */
    public BizException(String message, Throwable cause) {
        super(ResultCode.BIZ_ERROR, message, cause);
    }
}
