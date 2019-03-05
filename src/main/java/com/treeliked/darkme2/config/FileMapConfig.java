package com.treeliked.darkme2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 文件映射
 *
 * @author lqs2
 * @date 2018/9/23, Sun
 */
@Configuration
public class FileMapConfig implements WebMvcConfigurer {


    @Value("${my.mybatis.map-dir}")
    private String zipFilePath;


    /**
     * mybatis 生成文件下载前缀
     */
    public static String mybatisFileDownloadPathPrefix = "/file/mybatis/zip/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(mybatisFileDownloadPathPrefix + "**").addResourceLocations(zipFilePath);
    }
}
