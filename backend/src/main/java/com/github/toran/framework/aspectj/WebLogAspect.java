package com.github.toran.framework.aspectj;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 请求日志切面
 * 记录 API 请求参数、返回结果、耗时等信息
 *
 * @author toran
 */
@Slf4j
@Aspect
@Component
public class WebLogAspect {

    /**
     * 切入点：所有 Controller
     */
    @Pointcut("execution(* com.github.toran.module..controller..*.*(..))")
    public void webLog() {
    }

    /**
     * 环绕通知：记录请求日志
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取 Request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 记录请求信息
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String ip = getIpAddress(request);
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        log.info("========== Request Start ==========");
        log.info("URL            : {}", url);
        log.info("HTTP Method    : {}", method);
        log.info("IP             : {}", ip);
        log.info("Class Method   : {}", classMethod);
        log.info("Request Args   : {}", JSONUtil.toJsonStr(args));
        
        // 执行方法
        Object result = joinPoint.proceed();
        
        // 记录响应信息
        long duration = System.currentTimeMillis() - startTime;
        log.info("Response       : {}", JSONUtil.toJsonStr(result));
        log.info("Time-Consuming : {} ms", duration);
        log.info("========== Request End ==========");
        
        return result;
    }

    /**
     * 获取客户端真实 IP 地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个 IP 值，第一个为真实 IP
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            }
            return ip;
        }
        
        ip = request.getHeader("X-Real-IP");
        if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        
        return request.getRemoteAddr();
    }
}
