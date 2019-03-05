package com.treeliked.darkme2.controller;

import com.treeliked.darkme2.constant.SessionConstant;
import com.treeliked.darkme2.model.Response;
import com.treeliked.darkme2.model.ResultCode;
import com.treeliked.darkme2.model.dataobject.User;
import com.treeliked.darkme2.service.UserService;
import com.treeliked.darkme2.util.CookieUtils;
import com.treeliked.darkme2.util.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * user control
 *
 * @author lqs2
 * @date 2018/6/24, Sun
 */
@Slf4j
@RestController
public class UserController {


    private final UserService userService;

    /**
     * 是否邮箱flag
     */
    private static final String MAIL_FLAG = "@";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "lv")
    public Response loginValidate(@RequestParam("u1") String username, @RequestParam("u2") String pwd, boolean u3,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  HttpSession session) throws Exception {

        Response resp = new Response();

        User user;
        if (username.contains(MAIL_FLAG)) {
            // 邮箱登录
            user = userService.hasMatchUserByEmail(username, pwd);
        } else {
            user = userService.hasMatchUserByUsername(username, pwd);
        }
        if (user == null) {
            resp.setMessage("-1");
            return resp;
        }
        resp.setMessage("1");
        resp.setData0(user.getUsername());

        // 写入session
        SessionUtils.addAttribute(request, SessionConstant.KEY_OF_USER_NAME, user.getUsername());

        Cookie cookie = new Cookie(SessionConstant.COOKIE_OF_USER_INFO, session.getId());
        if (u3) {
            int len = SessionConstant.TIME_OF_REMEMBER;
            session.setMaxInactiveInterval(len);
            cookie.setMaxAge(len);

        } else {
            int len = SessionConstant.TIME_OF_TEMP;
            session.setMaxInactiveInterval(len);
            cookie.setMaxAge(len);
        }


        request.getServletContext().setAttribute(session.getId(), session);
        response.addCookie(cookie);
        return resp;

    }

    @GetMapping(value = "haveRememberMe")
    public Response haveRememberMe(@RequestParam("ssId") String sessionId, HttpServletRequest request, HttpSession session) {
        Response resp = new Response();
        HttpSession userSession = (HttpSession) request.getServletContext().getAttribute(sessionId);
        if (userSession != null) {
            String username = (String) userSession.getAttribute(SessionConstant.KEY_OF_USER_NAME);
            session.setAttribute(SessionConstant.KEY_OF_USER_NAME, username);
            resp.setMessage(username);
        } else {
            resp.setCode(ResultCode.FAIL);
        }
        return resp;
    }


    @PostMapping(value = "rv", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public Response registerValidate(@RequestBody User user) throws Exception {
        Response resp = new Response();
        int i = userService.insertUser(user);
        if (i != 1) {
            resp.setCode(ResultCode.FAIL);
        }
        return resp;
    }


    @GetMapping(value = "trunie")
    public Response testRegisterUsernameIsExist(@RequestParam("u1") String username) throws Exception {

        Response resp = new Response();

        int i = userService.hasMatchUsername(username);

        if (i == 1) {
            resp.setMessage("1");
        } else {
            resp.setMessage("0");
        }
        return resp;
    }

    @RequestMapping(value = "loginOut", method = {RequestMethod.GET})
    public void loginOut(String u1, HttpServletRequest request) {
        try {
            Cookie cookie = CookieUtils.getCookieByName(request, SessionConstant.KEY_OF_USER_NAME);
            if (cookie != null) {
                cookie.setMaxAge(0);
            }
            HttpSession session = (HttpSession) request.getServletContext().getAttribute(u1);
            if (session != null) {
                session.invalidate();
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}