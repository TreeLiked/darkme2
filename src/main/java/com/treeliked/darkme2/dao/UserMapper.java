package com.treeliked.darkme2.dao;

import java.util.List;

import com.treeliked.darkme2.dataobject.IUserDO;
import com.treeliked.darkme2.model.domain.IUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(String id);

    int insert(IUserDO record);

    IUserDO selectByPrimaryKey(String id);

    IUserDO selectByName(@Param("name") String name, @Param("pwd") String password);

    IUserDO selectHasName(@Param("name") String name);

    IUserDO selectByEmail(@Param("name") String email, @Param("pwd") String password);

    IUserDO selectHasEmail(@Param("name") String email);

    List<IUserDO> selectAll();

    int updateByPrimaryKey(IUser record);
}