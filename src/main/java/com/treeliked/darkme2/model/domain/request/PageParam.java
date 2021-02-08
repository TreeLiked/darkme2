package com.treeliked.darkme2.model.domain.request;

import lombok.Data;

/**
 * 分页查询基础模型
 *
 * @author lss
 * @date 2020/12/30, 周三
 */
@Data
public class PageParam {
    
    private int currentPage = 1;
    
    private int pageSize = 20;
}
