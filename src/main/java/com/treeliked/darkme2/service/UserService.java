package com.treeliked.darkme2.service;

import java.util.List;

import com.treeliked.darkme2.model.domain.IUser;

/**
 * 用户服务接口
 *
 * @author lqs2
 * @date 2018-12-19, Wed
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param user 创建的user
     * @return 1插入成功
     * @throws Exception 异常
     */
    int insertUser(IUser user) throws Exception;

    /**
     * 根据用户名查询指定的用户
     *
     * @param username 用户名
     * @param password 密码
     * @return user model
     * @throws Exception 抛出所有异常
     */
    IUser hasMatchUserByUsername(String username, String password) throws Exception;

    /**
     * 根据用户邮箱查询指定的用户
     *
     * @param email 邮箱
     * @param password 密码
     * @return user model
     * @throws Exception 抛出所有异常
     */
    IUser hasMatchUserByEmail(String email, String password) throws Exception;

    /**
     * 根据用户名查询指定的用户
     *
     * @param username 用户名
     * @param password 密码
     * @return user model
     * @throws Exception 抛出所有异常
     */
    IUser hasThisUsername(String username) throws Exception;

    /**
     * 根据用户邮箱查询指定的用户
     *
     * @param email 邮箱
     * @param password 密码
     * @return user model
     * @throws Exception 抛出所有异常
     */
    IUser hasThisEmail(String email) throws Exception;

    IUser getUserById(String userId);

    /**
     * 根据用户邮箱/用户名查询指定的用户
     *
     * @param name 用户名/邮箱
     * @param pwd 密码
     * @return user model
     * @throws Exception 抛出所有异常
     */
    IUser hasMatchUser(String name, String pwd) throws Exception;

    /**
     * 查询是否存在此用户
     *
     * @param name 用户名
     * @return >1则存在用户
     * @throws Exception 抛出所有异常
     */
    boolean hasMatchUsername(String key) throws Exception;

    /**
     * 获取所有的用户
     *
     * @return 用户集合
     */
    List<IUser> getAllUsers();

    /**
     * 根据用户名关键字检索用户
     * @param usernameKey 关键字
     * @return 用户集合
     */
    List<IUser> getUsersByKey(String usernameKey);
}
