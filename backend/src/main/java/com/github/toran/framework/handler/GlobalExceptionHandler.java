package com.github.toran.framework.handler;

import com.github.toran.common.constant.ResultCode;
import com.github.toran.common.core.domain.Result;
import com.github.toran.common.exception.BaseException;
import com.github.toran.common.exception.BizException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理系统异常、业务异常、参数校验异常等
 *
 * @author toran
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException e) {
        log.error("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理基础异常
     */
    @ExceptionHandler(BaseException.class)
    public Result<?> handleBaseException(BaseException e) {
        log.error("基础异常：{}", e.getMessage());
        Integer code = e.getCode() != null ? e.getCode() : ResultCode.INTERNAL_ERROR;
        return Result.error(code, e.getMessage());
    }

    /**
     * 处理参数校验异常（@Validated 注解触发）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.joining("; "));
        
        log.error("参数校验异常：{}", message);
        return Result.error(ResultCode.PARAM_VALID_ERROR, message);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String message = e.getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.joining("; "));
        
        log.error("参数绑定异常：{}", message);
        return Result.error(ResultCode.PARAM_VALID_ERROR, message);
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        
        log.error("约束违反异常：{}", message);
        return Result.error(ResultCode.PARAM_VALID_ERROR, message);
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public Result<?> handleAuthenticationException(AuthenticationException e) {
        log.error("认证异常：{}", e.getMessage());
        return Result.error(ResultCode.UNAUTHORIZED, "认证失败，请重新登录");
    }

    /**
     * 处理权限异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> handleAccessDeniedException(AccessDeniedException e) {
        log.error("权限异常：{}", e.getMessage());
        return Result.error(ResultCode.FORBIDDEN, "权限不足，无法访问");
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<?> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常：", e);
        return Result.error(ResultCode.INTERNAL_ERROR, "系统内部错误，请联系管理员");
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常：", e);
        return Result.error(ResultCode.INTERNAL_ERROR, "系统运行异常：" + e.getMessage());
    }

    /**
     * 处理所有未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.error(ResultCode.INTERNAL_ERROR, "系统异常，请联系管理员");
    }
}
