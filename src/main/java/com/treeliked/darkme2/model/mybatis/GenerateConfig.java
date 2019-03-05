package com.treeliked.darkme2.model.mybatis;

import lombok.Data;

import java.util.List;

/**
 * 自动生成mapper和object 的配置
 *
 * @author lqs2
 * @date 2018-12-28, Fri
 */
@Data
public class GenerateConfig {

    /**
     * 服务器ip
     */
    private String ip;

    /**
     * 服务器端口号
     */
    private String port;

    /**
     * 数据库名
     */
    private String dbName;



    /**
     * 连接账户名
     */
    private String name;


    /**
     * 连接密码
     */
    private String pwd;

    /**
     * 表名
     */
    private List<String> tableName;


    /**
     * 生成文件夹的目录
     */
    private String genDir;

    /**
     * 生成压缩文件的目录
     */
    private String zipDir;

}
