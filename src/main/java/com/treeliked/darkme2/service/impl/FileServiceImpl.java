package com.treeliked.darkme2.service.impl;

import com.treeliked.darkme2.model.dao.FileMapper;
import com.treeliked.darkme2.model.dataobject.File;
import com.treeliked.darkme2.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TODO
 *
 * @author lqs2
 * @date 2018-12-18, Tue
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class FileServiceImpl implements FileService {

    private FileMapper fileMapper;

    @Autowired
    public FileServiceImpl(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }


    @Override
    public String generateFileNo() throws Exception {
        return fileMapper.getFileRandomNo();
    }

    @Override
    public int insertFileRecord(File file) throws Exception {
        return fileMapper.insert(file);
    }

    @Override
    public File getFileByBringNo(String bringNo) throws Exception {
        return fileMapper.bringFileOut(bringNo);
    }

    @Override
    public File getFileByFileId(String id) throws Exception {
        return fileMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<File> getFileByUser(String username) throws Exception {
        return fileMapper.getFileByUser(username);
    }

    @Override
    public int deleteFile(String id) throws Exception {
        return fileMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<File> getPublicFile() {
        return fileMapper.selectByPublic();
    }
}
