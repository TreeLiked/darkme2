package com.treeliked.darkme2.config;

import com.treeliked.darkme2.model.Response;
import com.treeliked.darkme2.model.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常拦截
 *
 * @author lqs2
 * @date 2018/8/3, Fri
 */
@Slf4j
@ControllerAdvice(basePackages = {"com.treeliked.darkme2.controller"})
public class GlobalExceptionHandler {

    /**
     * error page message
     */
    private static final String DEFAULT_ERROR_MESSAGE = "Sorry，something went error . . .";

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response errorHandle(HttpServletRequest request, Exception e) {

        String url = request.getRequestURL().toString();
        log.error("Access to {} occur error -> \n{}", url, e);

        Response resp = new Response();

        resp.setCode(ResultCode.INTERNAL_SERVER_ERROR);
        resp.setMessage(DEFAULT_ERROR_MESSAGE);

        resp.setData0("RequestUrl: " + request.getRequestURL().toString());
        resp.setData1("ErrorMessage: " + e.getMessage());

        return resp;
    }
}
