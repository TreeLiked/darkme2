package com.treeliked.darkme2.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

import com.iutr.shared.model.Result;
import com.iutr.shared.utils.ResultUtils;
import com.treeliked.darkme2.constant.SessionConstant;
import com.treeliked.darkme2.model.Response;
import com.treeliked.darkme2.model.ResultCode;
import com.treeliked.darkme2.model.domain.IUser;
import com.treeliked.darkme2.service.UserService;
import com.treeliked.darkme2.util.CookieUtils;
import com.treeliked.darkme2.util.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * user control
 *
 * @author lqs2
 * @date 2018/6/24, Sun
 */
@Slf4j
@RestController
@RequestMapping("/api/user/")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 是否邮箱flag
     */
    private static final String MAIL_FLAG = "@";

    @PostMapping(value = "login")
    public Result<IUser> loginValidate(@RequestBody IUser user,
            HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

        Result<IUser> result = new Result<>();

        String username = user.getName();
        String pwd = user.getPassword();
        if (username.contains(MAIL_FLAG)) {
            // 邮箱登录
            user = userService.hasMatchUserByEmail(username, pwd);
        } else {
            user = userService.hasMatchUserByUsername(username, pwd);
        }
        if (user == null) {
            result.setSuccess(false);
            return result;
        }
        result.setSuccess(true);
        result.setData(user);

        // 写入session
        SessionUtils.addAttribute(request, SessionConstant.KEY_OF_USER_NAME, user.getName());
        SessionUtils.addAttribute(request, SessionConstant.KEY_OF_USER_ID, user.getId());
        // 是否用户点击记住我
        boolean rememberMe = true;
        int len = rememberMe ? SessionConstant.TIME_OF_REMEMBER : SessionConstant.TIME_OF_TEMP;
        session.setMaxInactiveInterval(len);
        request.getServletContext().setAttribute(session.getId(), session);

        // 写入cookie
        Cookie cookie = new Cookie(SessionConstant.COOKIE_OF_USER_INFO, session.getId());
        cookie.setMaxAge(len);
        cookie.setPath("/");
        response.addCookie(cookie);

        return result;

    }

    @GetMapping(value = "cls")
    public Result<IUser> checkLoginStatus(HttpServletRequest request, HttpSession session) {
        String userId = CookieUtils.getSessionUserId(request);
        if (StringUtils.isEmpty(userId)) {
            return ResultUtils.newFailedResult("没有登录信息");
        }
        IUser user = userService.getUserById(userId);
        if (user == null) {
            return ResultUtils.newFailedResult("非法用户");
        }
        session.setAttribute(SessionConstant.KEY_OF_USER_NAME, user.getName());
        session.setAttribute(SessionConstant.KEY_OF_USER_ID, user.getId());
        return ResultUtils.newSuccessfulResult(user);
    }

    @GetMapping(value = "dls")
    public Result<Void> dropLoginStatus(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        Cookie sessionIdCookie = CookieUtils.getCookieByName(request, SessionConstant.COOKIE_OF_USER_INFO);
        if (sessionIdCookie == null || StringUtils.isEmpty(sessionIdCookie.getValue())) {
            return ResultUtils.newSuccessfulResult();
        }
        String sessionId = sessionIdCookie.getValue();
        HttpSession userSession = (HttpSession) request.getServletContext().getAttribute(sessionId);
        // 删除session
        session.invalidate();
        if (userSession == null) {
            return ResultUtils.newSuccessfulResult();
        }
        userSession.invalidate();


        // 删除cookie
        sessionIdCookie.setMaxAge(0);
        response.addCookie(sessionIdCookie);

        return ResultUtils.newSuccessfulResult();
    }

    @PostMapping(value = "rv", consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public Response registerValidate(@RequestBody IUser user) throws Exception {
        Response resp = new Response();
        int i = userService.insertUser(user);
        if (i != 1) {
            resp.setCode(ResultCode.FAIL);
        }
        return resp;
    }

    @GetMapping(value = "trunie")
    public Result<Boolean> testRegisterUsernameIsExist(@RequestParam("u1") String username) throws Exception {
        return ResultUtils.newSuccessfulResult(userService.hasMatchUsername(username));
    }

    @RequestMapping(value = "loginOut", method = { RequestMethod.GET })
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

    @GetMapping("searchByKey")
    public Result<List<IUser>> searchUsersByNames(@RequestParam(value = "key", required = false) String key) {
        return ResultUtils.newSuccessfulResult(userService.getUsersByKey(key));
    }
}