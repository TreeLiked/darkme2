package com.treeliked.darkme2.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 同一天的对象结构
 *
 * @author lss
 * @date 2021/2/8, 周一
 */
@Getter
@Setter
public class DateData<T> {

    private String dateStr;
    private List<T> data;
}
