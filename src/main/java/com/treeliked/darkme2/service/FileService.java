package com.treeliked.darkme2.service;

import java.util.List;
import java.util.Map;

import com.iutr.shared.model.Result;
import com.treeliked.darkme2.dataobject.IFilePO;
import com.treeliked.darkme2.model.PageData;
import com.treeliked.darkme2.model.domain.IBaseFile;
import com.treeliked.darkme2.model.domain.IUser;
import com.treeliked.darkme2.model.domain.request.PageParam;

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
    int createFile(IBaseFile file) throws Exception;

    /**
     * 获取指定编号的文件的记录
     *
     * @param bringNo 文件编号
     * @return file model
     * @throws Exception 抛出所有异常
     */
    IBaseFile getFileByBringNo(String bringNo) throws Exception;

    /**
     * 根据文件id获取文件
     *
     * @param id 文件id
     * @return file model
     * @throws Exception 抛出所有异常
     */
    IBaseFile getFileByFileId(String id) throws Exception;

    /**
     * 查询用户的文件
     *
     * @param username 用户名
     * @return all file
     * @throws Exception 异常
     */
    List<IBaseFile> getFileByUser(String username) throws Exception;

    /**
     * 根据文件id删除指定的文件
     *
     * @param id 文件id
     * @return 删除成功1否则删除失败
     * @throws Exception 异常
     */
    int deleteFile(String id) throws Exception;

    /**
     * 获取公开文件
     *
     * @return 文件列表
     */
    PageData<IBaseFile> getPublicFile(PageParam pageParam, String key);

    /**
     * 获取个人私有文件
     *
     * @param pageParam 分页查询参数
     * @param key 模糊搜索关键字啊
     * @param userId 用户主键
     * @return 分页数据
     */
    PageData<IBaseFile> getUserFile(PageParam pageParam, String key, String userId);

    /**
     * 检查文件下载密码是否匹配
     *
     * @param fileId 文件ID
     * @param password 下载密码
     * @return 是否匹配
     */
    boolean checkFilePassword(String fileId, String password);

    /**
     * 删除文件
     *
     * @param username 用户名
     * @param fileId 文件id
     * @return 删除结果
     */
    Result<String> deleteFile(String username, String fileId);

    /**
     * 获取文件列表中包含作者和目标用户的所有账户的映射
     *
     * @param files 文件列表
     * @return 集合
     */
    Map<String, IUser> getFileUsersMap(List<IFilePO> files);
}
