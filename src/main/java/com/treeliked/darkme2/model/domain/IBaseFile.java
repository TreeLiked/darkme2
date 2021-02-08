package com.treeliked.darkme2.model.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件模型
 *
 * @author lss
 * @date 2020/12/29, 周二
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class IBaseFile extends IBase {

    /**
     * 文件编号，如果非公开文件则只能通过文件搜索
     */
    private String no;

    /**
     * 是否公开
     */
    private boolean open;

    /**
     * 存储时间
     */
    private int saveDays;

    /**
     * 文件名，文件大小（字节）
     */
    private String name;

    /**
     * 文件大小，单位：字节
     */
    private long size;

    /**
     * 上传作者
     */
    private IUser user;

    /**
     * 需要发送给的人，如果为空则没有指定
     */
    private IUser destUser;

    /**
     * 下载密码
     */
    private String password;

    /**
     * 下载备注
     */
    private String attach;

    /**
     * 存储桶ID
     */
    private String bucketId;

}
