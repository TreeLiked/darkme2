package com.treeliked.darkme2.util;

import java.util.Optional;

import com.treeliked.darkme2.dataobject.IFilePO;
import com.treeliked.darkme2.dataobject.IUserDO;
import com.treeliked.darkme2.model.domain.IBaseFile;
import com.treeliked.darkme2.model.domain.IUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

/**
 * model conversion
 *
 * @author lss
 * @date 2020/12/30, 周三
 */
public class TransferUtils {

    public static IFilePO toFileRecord(IBaseFile file) {
        return Optional.ofNullable(file)
                .map(f -> {
                    IFilePO filePo = new IFilePO();
                    BeanUtils.copyProperties(f, filePo, "gmtCreated", "gmtModified");
                    if (file.getUser() != null) {
                        filePo.setUserId(file.getUser().getId());
                    }
                    if (file.getDestUser() != null) {
                        filePo.setDestUserId(file.getDestUser().getId());
                    }
                    // 忽略时间
                    return filePo;
                })
                .orElse(null);
    }

    public static IBaseFile toFileModel(IFilePO filePo) {
        return Optional.ofNullable(filePo)
                .map(f -> {
                    IBaseFile file = new IBaseFile();
                    BeanUtils.copyProperties(f, file, "gmtCreated", "gmtModified", "password");
                    file.setGmtCreated(DateUtils.asLocalDateTime(f.getGmtCreated()));
                    file.setGmtModified(DateUtils.asLocalDateTime(f.getGmtModified()));
                    if (StringUtils.isNotEmpty(filePo.getUserId())) {
                        file.setUser(new IUser(filePo.getUserId()));
                    }
                    if (StringUtils.isNotEmpty(filePo.getDestUserId())) {
                        file.setDestUser(new IUser(filePo.getDestUserId()));
                    }
                    if (StringUtils.isNotEmpty(filePo.getPassword())) {
                        file.setPassword("YES");
                    }
                    return file;
                })
                .orElse(null);
    }

    public static IUser toUserModel(IUserDO userDO) {
        return Optional.ofNullable(userDO)
                .map(u -> {
                    IUser user = new IUser();
                    BeanUtils.copyProperties(u, user, "password");
                    return user;
                })
                .orElse(null);
    }
}
