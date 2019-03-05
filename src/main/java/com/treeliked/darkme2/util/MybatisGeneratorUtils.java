package com.treeliked.darkme2.util;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * mybatis 逆向工程工具类
 *
 * @author lqs2
 * @date 2018-12-28, Fri
 */
public class MybatisGeneratorUtils {


    /**
     * mybatis 配置文件路径
     */
    private static String configFilePath = "generator-config.xml";

    public static void generate(Properties properties) throws Exception {
        List<String> warnings = new ArrayList<>();
        File configFile = new File(configFilePath);

//        File file = new File("code");
//        if (file.exists()) {
//            file.delete();
//        }
//        file.mkdir();

        ConfigurationParser cp = new ConfigurationParser(properties, warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
        if (!warnings.isEmpty()) {
            for (String warn : warnings) {
                System.out.println(warn);
            }
        }
        System.out.println("生成成功！");
    }
}
