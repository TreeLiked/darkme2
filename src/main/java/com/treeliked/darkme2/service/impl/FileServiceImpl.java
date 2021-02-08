package com.treeliked.darkme2.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.iutr.shared.model.Result;
import com.iutr.shared.model.ResultStatus;
import com.iutr.shared.utils.ResultUtils;
import com.treeliked.darkme2.dao.FileMapper;
import com.treeliked.darkme2.dao.IFileMapper;
import com.treeliked.darkme2.dataobject.IFilePO;
import com.treeliked.darkme2.model.PageData;
import com.treeliked.darkme2.model.domain.IBaseFile;
import com.treeliked.darkme2.model.domain.IUser;
import com.treeliked.darkme2.model.domain.request.PageParam;
import com.treeliked.darkme2.service.FileService;
import com.treeliked.darkme2.service.UserService;
import com.treeliked.darkme2.util.TransferUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * TODO
 *
 * @author lqs2
 * @date 2018-12-18, Tue
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class FileServiceImpl implements FileService {

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private IFileMapper newFileMapper;

    @Autowired
    private UserService userService;

    private static Pattern p = Pattern.compile("^[a-z0-9A-Z]{8}$");

    @Override
    public String generateFileNo() throws Exception {
        return fileMapper.getFileRandomNo();
    }

    @Override
    public int createFile(IBaseFile file) throws Exception {
        // 根据用户名称查询到用户模型id
        if (file.getUser() != null) {
            file.setUser(userService.getUserById(file.getUser().getId()));
        }
        if (file.getDestUser() != null) {
            file.setDestUser(userService.getUserById(file.getDestUser().getId()));
        }
        return newFileMapper.insert(TransferUtils.toFileRecord(file));
    }

    @Override
    public IBaseFile getFileByBringNo(String bringNo) throws Exception {
        return fileMapper.bringFileOut(bringNo);
    }

    @Override
    public IBaseFile getFileByFileId(String id) {
        return TransferUtils.toFileModel(newFileMapper.selectByPrimaryKey(id));
    }

    @Override
    public List<IBaseFile> getFileByUser(String username) throws Exception {
        return fileMapper.getFileByUser(username);
    }

    @Override
    public int deleteFile(String id) throws Exception {
        return fileMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PageData<IBaseFile> getPublicFile(PageParam pageParam, String key) {
        if (pageParam == null) {
            pageParam = new PageParam();
        }

        key = StringUtils.isBlank(key) ? null : key.trim();

        List<IFilePO> filePos;
        int total;
        if (StringUtils.isNotEmpty(key) && p.matcher(key).matches()) {
            // 根据编号进行搜索
            IFilePO filePo = newFileMapper.selectByFileNo(key);
            filePos = Collections.singletonList(filePo);
            total = 1;
        } else {
            int cp = pageParam.getCurrentPage();
            int ps = pageParam.getPageSize();
            filePos = newFileMapper.selectAllOpenFile(cp, ps, key);
            // 查询总数
            total = newFileMapper.selectAllOpenFileCount(cp, ps, key);
        }

        PageData<IBaseFile> data = new PageData<>();
        data.setCurrentPage(pageParam.getCurrentPage());
        data.setPageSize(pageParam.getPageSize());
        data.setTotal(total);
        // 用户映射
        Map<String, IUser> userIdMap = getFileUsersMap(filePos);

        // 替换详细用户
        data.setData(replaceUserAndConvert(userIdMap, filePos));

        return data;
    }

    @Override
    public PageData<IBaseFile> getUserFile(PageParam pageParam, String key, String userId) {
        if (pageParam == null) {
            pageParam = new PageParam();
        }
        PageData<IBaseFile> pageData = new PageData<>();
        // 当前查询所有，不分页
        pageData.setCurrentPage(-1);
        pageData.setPageSize(-1);

        List<IFilePO> filePos = newFileMapper.selectByUser(userId, pageParam);

        Map<String, IUser> userIdMap = getFileUsersMap(filePos);

        pageData.setData(replaceUserAndConvert(userIdMap, filePos));
        return pageData;
    }

    @Override
    public boolean checkFilePassword(String fileId, String password) {
        if (StringUtils.isAnyEmpty(fileId, password)) {
            return true;
        }
        IFilePO filePo = newFileMapper.selectByPrimaryKey(fileId);
        if (filePo == null) {
            return false;
        }
        if (StringUtils.isEmpty(filePo.getPassword())) {
            return true;
        }
        return filePo.getPassword().equals(password);
    }

    @Override
    public Result<String> deleteFile(String username, String fileId) {
        if (StringUtils.isEmpty(username)) {
            return ResultUtils.newFailedResult("未登录的用户");
        }
        if (StringUtils.isEmpty(fileId)) {
            return ResultUtils.newFailedResult("删除失败，错误的文件编号");
        }
        IBaseFile fileToDel = getFileByFileId(fileId);
        if (fileToDel == null) {
            return ResultUtils.newFailedResult("删除失败，找不到文件");
        }
        if (fileToDel.getUser() == null) {
            return ResultUtils.newFailedResult("匿名上传的文件无法删除");
        }
        if (!StringUtils.equals(fileToDel.getUser().getName(), username)) {
            return ResultUtils.newFailedResult("权限不足，无法删除文件");
        }
        int i = fileMapper.deleteByPrimaryKey(fileId);
        if (i != 1) {
            return ResultUtils.newFailedResult("权限不足，无法删除文件", ResultStatus.UN_HANDLE_ERROR.getCode());
        }
        return ResultUtils.newSuccessfulResult();
    }

    @Override
    public Map<String, IUser> getFileUsersMap(List<IFilePO> files) {
        if (CollectionUtils.isEmpty(files)) {
            return new HashMap<>();
        }
        Map<String, IUser> userIdMap = new HashMap<>(files.size() * 2);
        // 查询所有用户
        List<IUser> allUsers = userService.getAllUsers();
        if (!CollectionUtils.isEmpty(allUsers)) {
            allUsers.forEach(user -> userIdMap.put(user.getId(), user));
        }
        return userIdMap;
    }

    private static List<IBaseFile> replaceUserAndConvert(Map<String, IUser> userIdMap, List<IFilePO> filePos) {
        if (CollectionUtils.isEmpty(userIdMap) || CollectionUtils.isEmpty(filePos)) {
            return new ArrayList<>();
        }
        return filePos.stream().map(TransferUtils::toFileModel).peek(bf -> {
            // 这边的用户只有ID，所以替换一下
            if (bf.getUser() != null) {
                bf.setUser(userIdMap.get(bf.getUser().getId()));
            }
            if (bf.getDestUser() != null) {
                bf.setDestUser(userIdMap.get(bf.getDestUser().getId()));
            }
        }).collect(Collectors.toList());

    }
}
