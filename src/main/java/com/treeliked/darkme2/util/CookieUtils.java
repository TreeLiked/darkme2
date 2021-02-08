package com.treeliked.darkme2.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.treeliked.darkme2.constant.SessionConstant;
import org.apache.commons.lang3.StringUtils;

/**
 * cookie 工具类
 *
 * @author lqs2
 * @date 2018/7/21, Sat
 */
public class CookieUtils {

    public static Cookie getCookieByName(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static String getSessionUserId(HttpServletRequest request) {

        Cookie sessionIdCookie = getCookieByName(request, SessionConstant.COOKIE_OF_USER_INFO);
        if (sessionIdCookie == null || StringUtils.isEmpty(sessionIdCookie.getValue())) {
            return null;
        }
        String sessionId = sessionIdCookie.getValue();
        HttpSession userSession = request.getSession();
        if (userSession == null) {
            return null;
        }
        String userId = (String) userSession.getAttribute(SessionConstant.KEY_OF_USER_ID);
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return userId;
    }

    public static boolean isLoginUser(HttpSession session) {
        return StringUtils.isNotEmpty(SessionUtils.getUserNameInSession(session));
    }
}