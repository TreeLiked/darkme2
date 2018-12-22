package com.treeliked.darkme2.controller;

import com.treeliked.darkme2.model.Response;
import com.treeliked.darkme2.model.ResultCode;
import com.treeliked.darkme2.model.dataobject.Memo;
import com.treeliked.darkme2.service.MemoService;
import com.treeliked.darkme2.util.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 标签 controller
 *
 * @author lqs2
 * @date 2018-12-22, Sat
 */
@Slf4j
@RestController
@RequestMapping("/api/memo/")
public class MemoController {

    private final MemoService memoService;

    @Autowired
    public MemoController(MemoService memoService) {
        this.memoService = memoService;
    }

    @PostMapping("newMemo")
    public Response createMemo(@RequestBody Memo memo, HttpSession session) throws Exception {
        log.info("{}", memo);
        Response resp = new Response();
        String username = SessionUtils.getUserNameInSession(session);
        memo.setMemoAuthor(username);
        int i = memoService.addNewMemo(memo);
        if (i != 1) {
            resp.setCode(ResultCode.FAIL);
        }
        return resp;
    }

    @GetMapping(value = "getMemo")
    public Response getMemoByMemoState(@RequestParam("finished") Boolean finished, HttpSession session) throws Exception {
        Response resp = new Response();
        String username = SessionUtils.getUserNameInSession(session);
        List<Memo> memos = memoService.getUserMemoByState(username, finished);
        resp.setData0(memos);
        return resp;
    }

    @PostMapping("modMyMemo")
    public Response modifyMyMemo(@RequestParam("id") String id, int toState, boolean isRemoved, HttpSession session) throws Exception {
        Response resp = new Response();
        String username = SessionUtils.getUserNameInSession(session);
        Memo opMemo = memoService.getMemoById(id);
        if (opMemo.getMemoAuthor().equals(username)) {
            int result = memoService.modUserMemo(id, toState, isRemoved);
            if (result == 1) {
                resp.setMessage("操作成功");
            } else {
                resp.setMessage("操作失败");
            }
        } else {
            resp.setCode(ResultCode.FAIL);
            resp.setMessage("非法操作");
        }
        return resp;
    }

    @GetMapping(value = "searchMemo")
    public Response searchMemo(String key, HttpSession session) throws Exception {
        Response resp = new Response();
        String username = SessionUtils.getUserNameInSession(session);
        List<Memo> memoByValue = memoService.getMemoByValue(username, key);
        resp.setData0(memoByValue);
        return resp;
    }
}
