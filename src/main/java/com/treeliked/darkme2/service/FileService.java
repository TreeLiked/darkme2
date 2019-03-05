package com.treeliked.darkme2.service;

import com.treeliked.darkme2.model.dataobject.File;

import java.util.List;

/**
 * 文件服务接口
 *
 * @author lqs2
 * @date 2018-12-18, Tue
 */
public interface FileService {

    /**
     * 生成不重复的文件编号
     *
     * @return 文件编号
     * @throws Exception 抛出所有异常
     */
    String generateFileNo() throws Exception;

    /**
     * 插入一条新的文件记录
     *
     * @param file file model
     * @return 返回1表示插入成功
     * @throws Exception 抛出所有异常
     */
    int insertFileRecord(File file) throws Exception;


    /**
     * 获取指定编号的文件的记录
     *
     * @param bringNo 文件编号
     * @return file model
     * @throws Exception 抛出所有异常
     */
    File getFileByBringNo(String bringNo) throws Exception;

    /**
     * 根据文件id获取文件
     *
     * @param id 文件id
     * @return file model
     * @throws Exception 抛出所有异常
     */
    File getFileByFileId(String id) throws Exception;


    /**
     * 查询用户的文件
     *
     * @param username 用户名
     * @return all file
     * @throws Exception 异常
     */
    List<File> getFileByUser(String username) throws Exception;

    /**
     * 根据文件id删除指定的文件
     *
     * @param id 文件id
     * @return 删除成功1否则删除失败
     * @throws Exception 异常
     */
    int deleteFile(String id) throws Exception;

    /**
     *  获取公开文件
     * @return 文件列表
     */
    List<File> getPublicFile();
}
