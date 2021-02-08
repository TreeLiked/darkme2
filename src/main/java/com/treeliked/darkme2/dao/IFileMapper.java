package com.treeliked.darkme2.dao;

import java.util.List;

import com.treeliked.darkme2.dataobject.IFilePO;
import com.treeliked.darkme2.model.domain.request.PageParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface IFileMapper {
    int deleteByPrimaryKey(String id);

    int insert(IFilePO record);

    IFilePO selectByPrimaryKey(String id);

    IFilePO selectByFileNo(String fileNo);

    List<IFilePO> selectByUser(@Param("userId") String userId, @Param("pageParam") PageParam pageParam);

    List<IFilePO> selectAll();

    int updateByPrimaryKey(IFilePO record);

    List<IFilePO> selectAllOpenFile(@Param("currentPage") int currentPage, @Param("pageSize") int pageSize,
            @Param("key") String key);

    int selectAllOpenFileCount(@Param("currentPage") int currentPage, @Param("pageSize") int pageSize,
            @Param("key") String key);

}