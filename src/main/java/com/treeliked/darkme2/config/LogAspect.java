package com.treeliked.darkme2.config;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

import com.treeliked.darkme2.model.Response;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 打印请求/响应信息
 *
 * @author lqs2
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("execution(public * com.treeliked.darkme2.controller.*.*(..))")
    public void webLog() {
    }

    /**
     * 拦截请求参数信息
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        String remoteIp = request.getRemoteAddr();
        String requestUrl = request.getRequestURL().toString();
        String requestMethod = request.getMethod();

        logger.info("...Request Start");
        logger.info("Remote IP: " + remoteIp);
        logger.info("Request URL: " + requestMethod + " " + requestUrl);
        Enumeration<String> enu = request.getParameterNames();
        int i = 1;
        while (enu.hasMoreElements()) {
            String name = enu.nextElement();
            String value = request.getParameter(name);
            if (value.length() <= 100) {
                logger.info("Param[{}]: name= {}, value= {}", i++, name, value);
            } else {
                logger.info("Param[{}]: name= {}, value= {}", i++, name, value.substring(0, 97));
            }
        }
    }

    /**
     * 在请求结束后打印响应
     */
    @AfterReturning(returning = "resp", pointcut = "webLog()")
    public void doAfterReturning(Object resp) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        String response;
        if (resp instanceof String) {
            response = (String) resp;
            logger.info("Response: {}", response);
        }
        if (resp instanceof Response) {
            logger.info("Response: {}", resp.toString());
        }
        logger.info("Request end...");
    }
}
