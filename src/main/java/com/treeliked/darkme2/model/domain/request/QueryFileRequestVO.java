package com.treeliked.darkme2.model.domain.request;

import lombok.Data;

/**
 * 文件查询模型
 *
 * @author lss
 * @date 2020/12/30, 周三
 */
@Data
public class QueryFileRequestVO {

    /**
     * 如果有fileId，只查询fileId
     */
    private String fileId;

    /**
     * 更具关键字查询
     */
    private String key;

    /**
     * 分页查询参数
     */
    private PageParam pageParam;

}
