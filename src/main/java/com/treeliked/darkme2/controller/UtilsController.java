package com.treeliked.darkme2.controller;

import com.treeliked.darkme2.model.Response;
import com.treeliked.darkme2.model.mybatis.GenerateConfig;
import com.treeliked.darkme2.service.MybatisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * util controller
 *
 * @author lqs2
 * @date 2018-12-28, Fri
 */
@RestController
@RequestMapping("/api/util")
public class UtilsController {


    private final MybatisService mybatisService;

    @Autowired
    public UtilsController(MybatisService mybatisService) {
        this.mybatisService = mybatisService;
    }

    @PostMapping(value = "/mybatis")
    public Response mybatisGenerator(@RequestBody GenerateConfig config, HttpServletRequest request) throws Exception {
        Response resp = new Response();
        resp.setMessage("FAIL");
        String relativePath = mybatisService.generateMapperAndObject(config);
        if (!"-1".equals(relativePath)) {
            boolean b1 = mybatisService.zipResultDir(config.getGenDir(), config.getZipDir());
            if (b1) {
                resp.setMessage("SUCCESS");
                resp.setData0("https://" + request.getServerName() + ":" + request.getServerPort() + relativePath);
                System.out.println(resp.getData0());
//                FileUtils.deleteQuietly(new File(config.getGenDir()));
//                FileUtils.deleteQuietly(f);
            }
        }
        return resp;
    }
}
