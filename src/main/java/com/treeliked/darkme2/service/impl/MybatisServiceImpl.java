package com.treeliked.darkme2.service.impl;

import com.treeliked.darkme2.config.FileMapConfig;
import com.treeliked.darkme2.model.mybatis.GenerateConfig;
import com.treeliked.darkme2.service.MybatisService;
import com.treeliked.darkme2.util.IdUtils;
import com.treeliked.darkme2.util.MybatisGeneratorUtils;
import com.treeliked.darkme2.util.ZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

/**
 * mybatis 工具服务
 *
 * @author lqs2
 * @date 2018-12-28, Fri
 */
@Service
public class MybatisServiceImpl implements MybatisService {


    /**
     * 生成文件夹目录
     */
    private static String genDir;

    /**
     * 压缩文件夹目录
     */
    private static String zipDir;

    private static Logger log = LoggerFactory.getLogger(MybatisServiceImpl.class);

    @Value("${my.mybatis.gen-dir}")
    public void setBaseDir(String genDir) {
        MybatisServiceImpl.genDir = genDir;
    }

    @Value("${my.mybatis.zip-dir}")
    public void setZipDir(String zipDir) {
        MybatisServiceImpl.zipDir = zipDir;
    }

    @Override
    public String generateMapperAndObject(GenerateConfig config) {
        String relativeDirId = "MYBATIS-" + IdUtils.get8Id().toUpperCase();

        String fullGenPath = genDir + relativeDirId;
        java.io.File file = new File(fullGenPath);

        System.out.println(fullGenPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        config.setGenDir(genDir + relativeDirId);
        config.setZipDir(zipDir + relativeDirId + ".zip");

        // 生成配置文件
        Properties properties = genProperties(config);

        // 根据配置文件生成文件

        boolean success = true;
        try {
            MybatisGeneratorUtils.generate(properties);
        } catch (Exception e) {
            log.error("", e);
            success = false;
        }
        return success ? FileMapConfig.mybatisFileDownloadPathPrefix + relativeDirId + ".zip" : "-1";
    }

    @Override
    public boolean zipResultDir(String srcDir, String destDir) throws Exception {
        ZipUtils.compress(srcDir, destDir);
        return true;
    }


    /**
     * 生成配置文件
     */
    private static Properties genProperties(GenerateConfig config) {

        Properties properties = new Properties();
        properties.setProperty("url", "jdbc:mysql://" + config.getIp() + ":" + config.getPort() + "/" + config.getDbName());
        properties.setProperty("userId", config.getName());
        properties.setProperty("password", config.getPwd());
        // 包名
        properties.setProperty("package", config.getGenDir());
        properties.setProperty("baseDir", "");


        // 要生成的表名

        AtomicReference<Byte> i = new AtomicReference<>((byte) 0);
        config.getTableName().forEach(v -> properties.setProperty("tableName" + i
                .getAndSet((byte) (i.get() + 1)), v));
        return properties;
    }


}
