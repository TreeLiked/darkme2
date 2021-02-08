package com.treeliked.darkme2.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.treeliked.darkme2.dao.UserMapper;
import com.treeliked.darkme2.dataobject.IUserDO;
import com.treeliked.darkme2.model.domain.IUser;
import com.treeliked.darkme2.service.UserService;
import com.treeliked.darkme2.util.IdUtils;
import com.treeliked.darkme2.util.TransferUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 用户服务接口实现
 *
 * @author lqs2
 * @date 2018-12-19, Wed
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 是否邮箱flag
     */
    private static final String MAIL_FLAG = "@";

    @Override
    public int insertUser(IUser user) {
        user.setId(IdUtils.get32Id());
        return userMapper.insert(user);
    }

    @Override
    public IUser hasMatchUserByUsername(String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return null;
        }
        return TransferUtils.toUserModel(userMapper.selectByName(username, password));
    }

    @Override
    public IUser hasMatchUserByEmail(String email, String password) {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
            return null;
        }
        return TransferUtils.toUserModel(userMapper.selectByEmail(email, password));
    }

    @Override
    public IUser hasThisUsername(String username) {
        return TransferUtils.toUserModel(userMapper.selectHasName(username));
    }

    @Override
    public IUser hasThisEmail(String email) {
        return TransferUtils.toUserModel(userMapper.selectHasEmail(email));
    }

    @Override
    public IUser getUserById(String userId) {
        return TransferUtils.toUserModel(userMapper.selectByPrimaryKey(userId));
    }

    @Override
    public IUser hasMatchUser(String name, String pwd) {
        return name.contains(MAIL_FLAG) ? hasMatchUserByEmail(name, pwd) : hasMatchUserByUsername(name, pwd);
    }

    @Override
    public boolean hasMatchUsername(String key) {
        if (StringUtils.isEmpty(key)) {
            return true;
        }
        return (key.contains(MAIL_FLAG) ? hasThisEmail(key) : hasThisUsername(key)) != null;
    }

    @Override
    public List<IUser> getAllUsers() {
        List<IUserDO> userDos = userMapper.selectAll();
        if (CollectionUtils.isEmpty(userDos)) {
            return new ArrayList<>();
        }
        return userDos.stream().map(TransferUtils::toUserModel).collect(Collectors.toList());
    }

    @Override
    public List<IUser> getUsersByKey(String usernameKey) {
        List<IUser> users = getAllUsers();
        if (CollectionUtils.isEmpty(users)) {
            return new ArrayList<>();
        }
        if (StringUtils.isBlank(usernameKey)) {
            return users;
        }
        usernameKey = usernameKey.trim();
        String finalUsernameKey = usernameKey;
        return users.stream()
                .filter(user -> user.getName().contains(finalUsernameKey))
                .collect(Collectors.toList());
    }
}
