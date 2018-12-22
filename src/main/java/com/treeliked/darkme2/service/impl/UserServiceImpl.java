package com.treeliked.darkme2.service.impl;

import com.treeliked.darkme2.model.dao.UserMapper;
import com.treeliked.darkme2.model.dataobject.User;
import com.treeliked.darkme2.service.UserService;
import com.treeliked.darkme2.util.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务接口实现
 *
 * @author lqs2
 * @date 2018-12-19, Wed
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    /**
     * 是否邮箱flag
     */
    private static final String MAIL_FLAG = "@";


    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    @Override
    public int insertUser(User user) throws Exception {
        user.setId(IdUtils.get32Id());
        return userMapper.insert(user);
    }

    @Override
    public User hasMatchUserByUsername(String username, String password) throws Exception {
        return userMapper.hasMatchUser1(username, password);
    }

    @Override
    public User hasMatchUserByEmail(String email, String password) throws Exception {
        return userMapper.hasMatchUser2(email, password);
    }

    @Override
    public User hasMatchUser(String name, String pwd) throws Exception {
        return name.contains(MAIL_FLAG) ? hasMatchUserByEmail(name, pwd) : hasMatchUserByUsername(name, pwd);
    }

    @Override
    public int hasMatchUsername(String name) throws Exception {
        int i;
        if (name.contains(MAIL_FLAG)) {
            i = userMapper.hasMatcherUsername2(name);
        } else {
            i = userMapper.hasMatcherUsername1(name);
        }
        return i;
    }
}
