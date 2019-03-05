package com.treeliked.darkme2.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * test
 *
 * @author lqs2
 * @date 2018-12-18, Tue
 */
@RestController
public class TestController {

    @RequestMapping("/test/cookie")
    public String cookie(@RequestParam("name") String name, HttpServletRequest request, HttpSession session) {
        //取出session中的browser
        Object nameSession = session.getAttribute("name");
        if (nameSession == null) {
            System.out.println("不存在session，设置name=" + name);
            session.setAttribute("name", name);
        } else {
            System.out.println("存在session，name=" + nameSession.toString());
            Date date = new Date(session.getCreationTime());
            System.out.println(date);
            int maxInactiveInterval = session.getMaxInactiveInterval();
            System.out.println(maxInactiveInterval);
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName() + " : " + cookie.getValue());
            }
        }
        return "index";
    }


    @RequestMapping("/test/cookie2")
    public String cookie2(@RequestParam("name") String name, HttpSession session) {
        session.getAttribute("");
        return "index";
    }

    @RequestMapping("/test/error")
    @ResponseBody
    public String cookie3() throws Exception {
        int i = 1 / 0;
        return "jello";
    }

    @RequestMapping("/test/sc")
    @ResponseBody
    public boolean cookie3(HttpServletRequest request, HttpSession session) throws Exception {

        HttpSession session1 = request.getSession();
        System.out.println(session);
        System.out.println(session1);
        System.out.println(session == session1);

        ServletContext sc1 = session.getServletContext();
        ServletContext sc2 = request.getServletContext();
        System.out.println(sc1);
        System.out.println(sc2);
        System.out.println(sc1 == sc2);
        return true;
    }


    @RequestMapping("/testx")
    public void testX(HttpServletRequest request) {
        System.out.println(request.getRequestURL().toString());
        System.out.println(request.getRequestURI());
    }

}

