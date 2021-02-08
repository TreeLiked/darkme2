package com.treeliked.darkme2.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 页面数据
 *
 * @author lss
 * @date 2021/2/7, 周日
 */
@Getter
@Setter
public class PageData<T> {
    private int currentPage;
    private int pageSize;
    private int total;
    private List<T> data;
}
