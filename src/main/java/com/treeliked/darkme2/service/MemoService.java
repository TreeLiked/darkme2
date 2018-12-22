package com.treeliked.darkme2.service;

import com.treeliked.darkme2.model.dataobject.Memo;

import java.util.List;

/**
 * 便签服务接口
 *
 * @author lqs2
 * @date 2018-12-18, Tue
 */
public interface MemoService {

    /**
     * 获取用户便签的数量
     *
     * @param username 用户名
     * @return 便签数
     * @throws Exception 抛出所有异常
     */
    int getMemoCountByUser(String username) throws Exception;


    /**
     * 创建新标签
     *
     * @param memo 便签
     * @return 1添加成功
     * @throws Exception 抛出所有异常
     */
    int addNewMemo(Memo memo) throws Exception;


    /**
     * 根据便签的状态获取用户标签
     *
     * @param username 用户名
     * @param finished 是否已经完成
     * @return memo 列表
     * @throws Exception 抛出所有异常
     */
    List<Memo> getUserMemoByState(String username, boolean finished) throws Exception;


    /**
     * 根据key搜索memo
     *
     * @param username 用户名
     * @param key 关键字
     * @return memo 列表
     * @throws Exception 抛出所有异常
     */
    List<Memo> getMemoByValue(String username, String key) throws Exception;

    /**
     * 更改memo状态
     *
     * @param id        memo id
     * @param toState   需要修改到的状态
     * @param isRemoved 是否删除
     * @return 1更新成功
     * @throws Exception 抛出所有异常
     */
    int modUserMemo(String id, int toState, boolean isRemoved) throws Exception;

    /**
     * 删除指定id 的memo
     *
     * @param id memo id
     * @return 1删除成功
     * @throws Exception 抛出所有异常
     */
    int deleteMemoById(String id) throws Exception;

    /**
     * 根据memo id 获取指定的memo
     *
     * @param id memo id
     * @return memo
     * @throws Exception 抛出所有异常
     */
    Memo getMemoById(String id) throws Exception;


}
