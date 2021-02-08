package com.treeliked.darkme2.config;

import javax.servlet.http.HttpServletRequest;

import com.iutr.shared.model.Result;
import com.iutr.shared.model.ResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常拦截
 *
 * @author lqs2
 * @date 2018/8/3, Fri
 */
@Slf4j
@ControllerAdvice(basePackages = { "com.treeliked.darkme2.controller" })
public class GlobalExceptionHandler {

    /**
     * error page message
     */
    private static final String DEFAULT_ERROR_MESSAGE = "Sorry，something went error . . .";

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<?> errorHandle(HttpServletRequest request, Exception e) {

        String url = request.getRequestURL().toString();
        log.error("Access to {} occur error -> \n{}", url, e);

        Result<?> result = new Result<>();

        result.setCode(ResultStatus.UN_HANDLE_ERROR.getCode());
        result.setMessage(e.getMessage());
        return result;
    }
}
