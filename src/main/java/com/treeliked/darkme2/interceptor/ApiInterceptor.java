package com.treeliked.darkme2.interceptor;

import com.treeliked.darkme2.constant.SessionConstant;
import com.treeliked.darkme2.model.Response;
import com.treeliked.darkme2.model.ResultCode;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * api拦截，检查会话是否超时
 *
 * @author lqs2
 * @date 2018-12-18, Tue
 */
public class ApiInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String username = (String) request.getSession().getAttribute(SessionConstant.KEY_OF_USER_NAME);
        // 是否session中存在用户名
        if (StringUtils.isEmpty(username)) {
            // 会话已经过期
            Response resp = new Response();
            resp.setCode(ResultCode.FAIL);
            resp.setMessage(SessionConstant.TIME_OUT);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
