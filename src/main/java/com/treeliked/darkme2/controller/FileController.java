package com.treeliked.darkme2.controller;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.ResponseHeaderOverrides;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.utils.DateUtils;
import com.treeliked.darkme2.constant.CosConstant;
import com.treeliked.darkme2.constant.FileStorageConstant;
import com.treeliked.darkme2.constant.SessionConstant;
import com.treeliked.darkme2.model.Response;
import com.treeliked.darkme2.model.ResultCode;
import com.treeliked.darkme2.model.dataobject.File;
import com.treeliked.darkme2.service.FileService;
import com.treeliked.darkme2.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * file control
 *
 * @author lqs2
 * @date 2018/6/24, Sun
 */
@Slf4j
@RestController
@Transactional(rollbackFor = RuntimeException.class)
@RequestMapping("/api/file/")
public class FileController {


    private final FileService fileService;

    private static COSClient client;


    private COSClient getClient() {
        if (client == null) {
            COSCredentials cred = new BasicCOSCredentials(CosConstant.SECRET_ID, CosConstant.SECRET_KEY);
            ClientConfig clientConfig = new ClientConfig(new Region(CosConstant.REGION));
            client = new COSClient(cred, clientConfig);
        }
        return client;
    }

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping(value = "generateFileNo")
    public Response generateFileNo(@RequestParam("fileName") String fileName, HttpSession session) throws Exception {
        Response resp = new Response();
        String no = fileService.generateFileNo();
        String suffix = FileUtils.getSuffixByName(fileName);
        resp.setCode(ResultCode.SUCCESS);
        if (!StringUtils.isEmpty(SessionUtils.getUserNameInSession(session))) {
            resp.setMessage("1");
        } else {
            resp.setMessage("0");
        }
        resp.setData0(no);
        resp.setData1(no + "." + suffix);
        return resp;
    }


    @PostMapping("generateNewFile")
    public Response generateNewFile(@RequestParam("fileNo") String fileNo, @RequestBody File record, HttpSession session) throws Exception {

        log.info("{}...", record);
        Response resp = new Response();
        resp.setData0(record);

        record.setId(IdUtils.get32Id());
        String suffix = FileUtils.getSuffixByName(record.getFileName());
        record.setFileBringId(fileNo);
        record.setFileBucketId(fileNo + "." + suffix);

        String filePostAuthor = (String) session.getAttribute(SessionConstant.KEY_OF_USER_NAME);
        if (StringUtils.isEmpty(filePostAuthor)) {
            record.setFilePostAuthor(FileStorageConstant.FILE_WITH_ANONYMOUS_AUTHOR);
            record.setFileSaveDays(FileStorageConstant.FILE_SAVE_SHORT_TIME);
        } else {
            record.setFilePostAuthor(filePostAuthor);
            record.setFileSaveDays(FileStorageConstant.FILE_SAVE_LONG_TIME);
        }

        int i = fileService.insertFileRecord(record);
        if (i != 1) {
            resp.setCode(ResultCode.FAIL);
        }
        return resp;
    }


    @GetMapping(value = "getObjectDetail")
    public Response getObjectDetail(@RequestParam("bringNo") String bringNo, HttpSession session) throws Exception {
        File file = fileService.getFileByBringNo(bringNo);
        Response resp = new Response();
        if (file != null) {
            if (!StringUtils.isEmpty(file.getFileDestination())) {
                // 没有权限下载文件
                resp.setMessage("-1");
                String username = SessionUtils.getUserNameInSession(session);

                if (username.equals(file.getFileDestination()) || username.equals(file.getFilePostAuthor())) {
                    // 允许下载文件
                    resp.setMessage("1");
                    resp.setData0(file);
                }

            } else {
                resp.setMessage("1");
                resp.setData0(file);
            }
        } else {
            // 文件不存在
            resp.setMessage("0");
        }
        return resp;
    }


    @GetMapping("getPublic")
    public Response getPublicFile() {
        Response resp = new Response();
        List<File> file = fileService.getPublicFile();
        resp.setMessage("1");
        resp.setData0(file);
        return resp;
    }


    @GetMapping(value = "doDownload")
    public void downloadFile(@RequestParam("id") String id,
                             HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        File file = fileService.getFileByFileId(id);
        ServletContext context = request.getServletContext();
        String mimeType = context.getMimeType(file.getFileName());
        System.out.println(mimeType);
        String bucketName = file.getFileSaveDays() > FileStorageConstant.FILE_SAVE_SHORT_TIME ? CosConstant.BUCKET_NAME60 : CosConstant.BUCKET_NAME1;
        // key in bucket
        String key = file.getFileBucketId();
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, key, HttpMethodName.GET);

        //设置下载时返回的 http 头
        ResponseHeaderOverrides responseHeaders = new ResponseHeaderOverrides();

        String responseContentLanguage = "zh-CN";
        String responseCacheControl = "no-cache";
        String cacheExpireStr = DateUtils.formatRFC822Date(new Date(System.currentTimeMillis() + 24L * 3600L * 1000L));
        responseHeaders.setContentType(mimeType);
        responseHeaders.setContentLanguage(responseContentLanguage);
        responseHeaders.setContentDisposition(DownloadUtils.getFileDownloadHeaderStr(file.getFileName()));
        responseHeaders.setCacheControl(responseCacheControl);
        responseHeaders.setExpires(cacheExpireStr);
        req.setResponseHeaders(responseHeaders);
        // 设置签名在半个小时后过期，默认5分钟
        Date expirationDate = new Date(System.currentTimeMillis() + 30L * 60L * 1000L);
        req.setExpiration(expirationDate);
        URL url = getClient().generatePresignedUrl(req);
        // 打开新窗口下载文件
        response.getWriter().write("<script>window.open('" + url + "');</script>");
    }


    @GetMapping(value = "rmFile")
    public Response removeFile(@RequestParam("id") String id, HttpSession session) throws Exception {
        Response resp = new Response();

        // 获取此文件的信息
        File file = fileService.getFileByFileId(id);

        String username = (String) session.getAttribute(SessionConstant.KEY_OF_USER_NAME);

        if (!username.equals(file.getFilePostAuthor())) {
            resp.setCode(ResultCode.FAIL);
        }

        String bucketName = CosUtils.bucketName1;
        if (file.getFileSaveDays() > FileStorageConstant.FILE_SAVE_SHORT_TIME) {
            bucketName = CosUtils.bucketName60;
        }
        int i = fileService.deleteFile(id);
        CosUtils.dropFile(file.getFileBucketId(), bucketName);

        if (i == 1) {
            resp.setMessage("1");
        } else {
            resp.setCode(ResultCode.FAIL);
        }
        return resp;
    }


    @PostMapping(value = "hello-world")
    public Response getCosKeyAndSec() {
        Response resp = new Response();
        resp.setData0(CosConstant.SECRET_ID);
        resp.setData1(CosConstant.SECRET_KEY);
        return resp;
    }

}
