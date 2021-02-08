package com.treeliked.darkme2.controller;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URL;
import java.text.MessageFormat;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.iutr.shared.model.Result;
import com.iutr.shared.utils.ResultUtils;
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
import com.treeliked.darkme2.constant.SessionConstant;
import com.treeliked.darkme2.model.PageData;
import com.treeliked.darkme2.model.Response;
import com.treeliked.darkme2.model.domain.IBaseFile;
import com.treeliked.darkme2.model.domain.IUser;
import com.treeliked.darkme2.model.domain.request.PageParam;
import com.treeliked.darkme2.service.FileService;
import com.treeliked.darkme2.util.CookieUtils;
import com.treeliked.darkme2.util.DownloadUtils;
import com.treeliked.darkme2.util.FileUtils;
import com.treeliked.darkme2.util.IdUtils;
import com.treeliked.darkme2.util.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * file control
 *
 * @author lqs2
 * @date 2018/6/24, Sun
 */
@Slf4j
@RestController
@Transactional(rollbackFor = RuntimeException.class)
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileService fileService;

    private static COSClient client;

    private COSClient getClient() {
        if (client == null) {
            COSCredentials cred = new BasicCOSCredentials(CosConstant.SECRET_ID, CosConstant.SECRET_KEY);
            ClientConfig clientConfig = new ClientConfig(new Region(CosConstant.REGION));
            client = new COSClient(cred, clientConfig);
        }
        return client;
    }

    @GetMapping(value = "/gfn")
    public Result<String> generateFileNo(@RequestParam("fileName") String fileName, HttpSession session)
            throws Exception {
        Result<String> result = new Result<>();
        String fileNo = fileService.generateFileNo();
        String suffix = FileUtils.getSuffixByName(fileName);
        if (StringUtils.isNotEmpty(fileNo) && StringUtils.isNotEmpty(suffix)) {
            result.setSuccess(true);
            result.setData(MessageFormat.format("{0}.{1}", fileNo, FileUtils.getSuffixByName(fileName)));
        } else {
            result.setMessage("生成文件编号失败或文件格式不正确");
            result.setSuccess(false);
        }
        return result;
    }

    @PostMapping("/create")
    public Result<IBaseFile> createNewFile(@RequestBody IBaseFile record, HttpSession session) throws Exception {

        log.info("Create File... {}", JSON.toJSONString(record));

        // 这里的文件编号带了文件格式后缀
        String fileNo = record.getNo();
        record.setId(IdUtils.get32Id());
        record.setNo(fileNo.substring(0, fileNo.indexOf(".")));
        record.setBucketId(record.getNo() + "." + FileUtils.getSuffixByName(record.getName()));

        String authorUserId = (String) session.getAttribute(SessionConstant.KEY_OF_USER_ID);
        if (StringUtils.isNotEmpty(authorUserId)) {
            IUser user = new IUser();
            user.setId(authorUserId);
            record.setUser(user);
        }

        int i = fileService.createFile(record);
        if (i != 1) {
            return ResultUtils.newFailedResult("文件上传失败");
        }
        return ResultUtils.newSuccessfulResult(record);
    }

    @GetMapping(value = "/getObjectDetail")
    public Result<IBaseFile> getObjectDetail(@RequestParam("bringNo") String bringNo, HttpSession session)
            throws Exception {
        IBaseFile file = fileService.getFileByBringNo(bringNo);
        if (file == null) {
            return ResultUtils.newFailedResult("文件不存在或被删除");
        }
        // 什么情况下可以查看文件呢
        if (file.isOpen()) {
            // 任何人都可以查看详情
            return ResultUtils.newSuccessfulResult(file);
        }
        // 文件没有公开，则只有上传用户或者指定的目标用户可以查看文件详情
        String username = SessionUtils.getUserNameInSession(session);
        if (StringUtils.isEmpty(username)) {
            return ResultUtils.newFailedResult("您无权查看此文件");
        }
        // 目标用户可以查看
        if (file.getDestUser() != null && username.equals(file.getDestUser().getName())) {
            return ResultUtils.newSuccessfulResult(file);
        }
        if (file.getUser() != null && username.equals(file.getUser().getName())) {
            return ResultUtils.newSuccessfulResult(file);
        }
        return ResultUtils.newFailedResult("您无权查看此文件");
    }

    @PostMapping("/getPublic")
    public Result<PageData<IBaseFile>> getPublicFile(
            @RequestParam(value = "key", required = false) String blurSearchKey,
            @RequestBody(required = false) PageParam pageParam) {
        Result<PageData<IBaseFile>> res = new Result<>();
        res.setSuccess(true);
        PageData<IBaseFile> data = fileService.getPublicFile(pageParam, blurSearchKey);
        res.setData(data);
        return res;
    }

    @PostMapping("/getPrivate")
    public Result<PageData<IBaseFile>> getPrivateFile(
            @RequestParam(value = "key", required = false) String blurSearchKey,
            @RequestBody(required = false) PageParam pageParam, HttpServletRequest request) {

        String userId = CookieUtils.getSessionUserId(request);
        if (StringUtils.isEmpty(userId)) {
            return ResultUtils.newFailedResult("没有登录信息");
        }
        Result<PageData<IBaseFile>> res = new Result<>();
        res.setSuccess(true);
        PageData<IBaseFile> data = fileService.getUserFile(pageParam, blurSearchKey, userId);
        List<IBaseFile> fileList = data.getData();
        if (CollectionUtils.isEmpty(fileList)) {
            return ResultUtils.newSuccessfulResult();
        }
        // 时间降序排列
        fileList.sort(
                (f1, f2) -> (int) (f2.getGmtCreated().toInstant(ZoneOffset.of("+8")).toEpochMilli() - f1.getGmtCreated()
                        .toInstant(ZoneOffset.of("+8")).toEpochMilli()));

        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //
        //List<DateData<IBaseFile>> finalData = new ArrayList<>();
        //
        //for (IBaseFile bf : fileList) {
        //    String dateStr = bf.getGmtCreated().format(formatter);
        //    if(finalData)
        //}
        return ResultUtils.newSuccessfulResult(data);

    }

    @GetMapping(value = "/checkPassword")
    public Result<Void> checkFilePassword(@RequestParam("id") String fileId, @RequestParam("pwd") String password)
            throws InterruptedException {
        Thread.sleep(2000);
        boolean checkRes = fileService.checkFilePassword(fileId, password);
        if (checkRes) {
            return ResultUtils.newSuccessfulResult();
        }
        return ResultUtils.newFailedResult("密码校验失败");
    }

    @RequestMapping(value = "/doDownload")
    public Result<String> downloadFile(@RequestParam("id") String id, @RequestParam("pwd") String password,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        IBaseFile file = fileService.getFileByFileId(id);
        boolean checkFilePassword = fileService.checkFilePassword(id, password);
        if (!checkFilePassword) {
            return ResultUtils.newFailedResult("密码校验失败");
        }
        ServletContext context = request.getServletContext();
        String mimeType = context.getMimeType(file.getName());
        // key in bucket
        String key = file.getBucketId();
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(CosConstant.BUCKET_NAME1, key,
                HttpMethodName.GET);

        //设置下载时返回的 http 头
        ResponseHeaderOverrides responseHeaders = new ResponseHeaderOverrides();

        String responseContentLanguage = "zh-CN";
        String responseCacheControl = "no-cache";
        String cacheExpireStr = DateUtils.formatRFC822Date(new Date(System.currentTimeMillis() + 24L * 3600L * 1000L));
        responseHeaders.setContentType(mimeType);
        responseHeaders.setContentLanguage(responseContentLanguage);
        responseHeaders.setContentDisposition(DownloadUtils.getFileDownloadHeaderStr(file.getName()));
        responseHeaders.setCacheControl(responseCacheControl);
        responseHeaders.setExpires(cacheExpireStr);
        req.setResponseHeaders(responseHeaders);
        // 设置签名在半个小时后过期，默认5分钟
        Date expirationDate = new Date(System.currentTimeMillis() + 30L * 60L * 1000L);
        req.setExpiration(expirationDate);
        URL url = getClient().generatePresignedUrl(req);
        String link = url.toString();
        link = "https://cos.iutr.cn" + link.substring(link.indexOf("com") + 3);
        // 打开新窗口下载文件
        //response.getWriter().write("<script>window.open('" + link + "', '_blank');</script>");
        return ResultUtils.newSuccessfulResult(link);
    }

    @GetMapping(value = "rmFile")
    public Result<String> removeFile(@RequestParam("id") String id, HttpSession session) throws Exception {

        // 获取已经登录用户名
        String username = (String) session.getAttribute(SessionConstant.KEY_OF_USER_NAME);
        if (StringUtils.isEmpty(username)) {
            return ResultUtils.newFailedResult("您没有权限删除此文件");

        }
        return fileService.deleteFile(username, id);

        //// 获取此文件的信息
        //IBaseFile file = fileService.getFileByFileId(id);
        //
        //if(file == null) {
        //    return ResultUtils.newFailedResult("删除失败，找不到文件");
        //}
        //
        //
        //if (!username.equals(file.getFilePostAuthor())) {
        //    resp.setCode(ResultCode.FAIL);
        //}
        //
        //String bucketName = CosUtils.bucketName1;
        //if (file.getFileSaveDays() > FileStorageConstant.FILE_SAVE_SHORT_TIME) {
        //    bucketName = CosUtils.bucketName60;
        //}
        //int i = fileService.deleteFile(id);
        //CosUtils.dropFile(file.getFileBucketId(), bucketName);
        //
        //if (i == 1) {
        //    resp.setMessage("1");
        //} else {
        //    resp.setCode(ResultCode.FAIL);
        //}
        //return resp;
    }

    @PostMapping(value = "/hello-world")
    public Response getCosKeyAndSec() {
        Response resp = new Response();
        resp.setData0(CosConstant.SECRET_ID);
        resp.setData1(CosConstant.SECRET_KEY);
        return resp;
    }

}
