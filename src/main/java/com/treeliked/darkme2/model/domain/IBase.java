package com.treeliked.darkme2.model.domain;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * i base model
 *
 * @author lss
 * @date 2020/12/29, 周二
 */
@Data
public class IBase {

    private String id;

    private transient LocalDateTime gmtCreated;
    private transient LocalDateTime gmtModified;
}
