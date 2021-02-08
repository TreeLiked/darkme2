package com.treeliked.darkme2.controller;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import com.treeliked.darkme2.constant.SessionConstant;
import com.treeliked.darkme2.model.Response;
import com.treeliked.darkme2.model.ResultCode;
import com.treeliked.darkme2.service.FileService;
import com.treeliked.darkme2.util.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 页面控制器
 *
 * @author lqs2
 */
@Slf4j
@Controller
public class PageController {

    @Autowired
    private FileService fileService;

    @RequestMapping("/")
    public String goMain(HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        return "main";
    }

    @RequestMapping("hello")
    public String sayHello() {
        return "hello";
    }

    @RequestMapping("index")
    public String main(HttpServletResponse response, Model model) {
        response.setContentType("text/html;charset=utf-8");
        model.addAttribute("subFrame", "hello");
        return "index";
    }

    @RequestMapping("myFile")
    public String mFile(HttpServletResponse response, HttpSession session, Model model) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String username = (String) session.getAttribute(SessionConstant.KEY_OF_USER_NAME);
        if (username != null) {
            List mFileList = fileService.getFileByUser(username);
            mFileList.forEach(System.out::println);
            model.addAttribute("mFilelist", mFileList);
            model.addAttribute("file_amount", mFileList.size());
        }
        return "file";
    }

    @RequestMapping("myMemo")
    public String mMemo() {
        return "memo";
    }

    @RequestMapping("myMsg")
    public String myMessage() {
        return "msg";
    }

    @RequestMapping("myFriend")
    public String mFriend() {
        return "friend";
    }

    @RequestMapping("goSource")
    public void goGit(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://github.com/TreeLiked/darkme2");
    }

    @RequestMapping("stroke")
    public String displayOneStroke() {
        return "onestroke";
    }

    @RequestMapping("countAll")
    public @ResponseBody
    Response getCountFlags(HttpSession session) throws Exception {
        Response resp = new Response();
        String username = SessionUtils.getUserNameInSession(session);
        if (username != null) {
            int result1 = fileService.getFileByUser(username).size();
            //int result2 = memoService.getMemoCountByUser(username);
            resp.setData0(result1);
            //resp.setData1(result2);
        } else {
            resp.setCode(ResultCode.FAIL);
        }
        return resp;
    }
}