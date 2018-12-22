package com.treeliked.darkme2.util;

import com.treeliked.darkme2.constant.SessionConstant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * 会话工具类
 *
 * @author lqs2
 * @date 2018-12-19, Wed
 */
public class SessionUtils {


    /**
     * 添加会话
     *
     * @param session 会话
     * @param k       key
     * @param v       value
     */
    public static void addAttribute(HttpSession session, String k, Object v) {
        if (session == null) {
            return;
        }
        session.setAttribute(k, v);
    }

    /**
     * 添加会话
     *
     * @param request 请求
     * @param k       key
     * @param v       value
     */
    public static void addAttribute(HttpServletRequest request, String k, Object v) {
        if (request == null) {
            return;
        }
        addAttribute(request.getSession(), k, v);
    }


    /**
     * 添加会话
     *
     * @param request 请求
     * @param attrs   所有会话
     */
    public static void addAttributes(HttpServletRequest request, HashMap<String, Object> attrs) {
        if (request == null) {
            return;
        }
        if (attrs == null || attrs.size() == 0) {
            return;
        }
        attrs.forEach((k, v) -> addAttribute(request.getSession(), k, v));
    }


    /**
     * 获取会话中的用户名
     *
     * @param session 会话
     * @return 用户名
     */
    public static String getUserNameInSession(HttpSession session) {
        return (String) session.getAttribute(SessionConstant.KEY_OF_USER_NAME);
    }
}
