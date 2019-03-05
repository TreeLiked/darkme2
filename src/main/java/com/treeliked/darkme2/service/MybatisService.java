package com.treeliked.darkme2.service;

import com.treeliked.darkme2.model.mybatis.GenerateConfig;

/**
 * TODO
 *
 * @author lqs2
 * @date 2018-12-28, Fri
 */
public interface MybatisService {


    /**
     * 生成mapper 和 object
     *
     * @param config 连接以及需要生成信息的配置
     * @return 文件夹的路径
     * @throws Exception 抛出所有异常
     */
    String generateMapperAndObject(GenerateConfig config) throws Exception;


    /**
     * 压缩生成后的文件
     *
     * @param srcDir  生成文件夹
     * @param destDir 目的文件夹
     * @return true则压缩成功
     * @throws Exception 抛出所有异常
     */
    boolean zipResultDir(String srcDir, String destDir) throws Exception;
}
