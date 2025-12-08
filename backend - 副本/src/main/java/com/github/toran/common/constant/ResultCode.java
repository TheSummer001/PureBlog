package com.github.toran.common.constant;

/**
 * 响应状态码常量
 *
 * @author toran
 */
public interface ResultCode {

    /**
     * 成功
     */
    int SUCCESS = 200;

    /**
     * 业务异常
     */
    int BIZ_ERROR = 400;

    /**
     * 未认证
     */
    int UNAUTHORIZED = 401;

    /**
     * 无权限
     */
    int FORBIDDEN = 403;

    /**
     * 资源不存在
     */
    int NOT_FOUND = 404;

    /**
     * 系统错误
     */
    int INTERNAL_ERROR = 500;

    /**
     * 参数校验失败
     */
    int PARAM_VALID_ERROR = 400;

    /**
     * 数据库操作失败
     */
    int DB_ERROR = 500;
}
