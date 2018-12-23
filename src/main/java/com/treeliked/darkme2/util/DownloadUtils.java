package com.treeliked.darkme2.util;


import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 工具类
 *
 * @author lqs2
 */
public class DownloadUtils {

    /**
     * 微软ie 浏览器标志
     */
    private static final String BROWSER_IE = "MSIE";

    /**
     * 火狐浏览器
     */
    private static final String BROWSER_FIREFOX = "Firefox";

    /**
     * 解决不同浏览器的中文乱码问题
     *
     * @param agent    浏览器标示
     * @param filename 文件名
     * @return 编码后的content-position
     * @throws UnsupportedEncodingException 不支持编码
     */
    public static String getName(String agent, String filename) throws UnsupportedEncodingException {
        if (agent.contains(BROWSER_IE)) {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        } else if (agent.contains(BROWSER_FIREFOX)) {
            // 火狐浏览器
            BASE64Encoder base64Encoder = new BASE64Encoder();
            filename = "=?utf-8?B?" + base64Encoder.encode(filename.getBytes(StandardCharsets.UTF_8)) + "?=";
        } else {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }

    /**
     * 其中encoded_text是经过RFC 3986的“百分号URL编码”规则处理过的文件名
     * </pre>
     *
     * @param response 响应
     * @param filename 文件名
     */
    public static void setFileDownloadHeader(HttpServletResponse response, String filename) {
        response.setHeader("Content-Disposition", getFileDownloadHeaderStr(filename));
    }

    /**
     * 获取响应头的str，解决中文乱码
     *
     * @param filename 文件名
     * @return str
     */
    public static String getFileDownloadHeaderStr(String filename) {
        String headerValue = "attachment;";
        headerValue += " filename=\"" + encodeURIComponent(filename) + "\";";
        headerValue += " filename*=utf-8''" + encodeURIComponent(filename);
        return headerValue;
    }

    /**
     * <pre>
     * 符合 RFC 3986 标准的“百分号URL编码”
     * 在这个方法里，空格会被编码成%20，而不是+
     * 和浏览器的encodeURIComponent行为一致
     * </pre>
     *
     * @param value 需要编码的值
     * @return 编码后的值
     */
    public static String encodeURIComponent(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
