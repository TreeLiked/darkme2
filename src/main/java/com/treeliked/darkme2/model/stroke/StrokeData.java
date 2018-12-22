package com.treeliked.darkme2.model.stroke;

import lombok.Data;

/**
 * 一笔画布局model
 *
 * @author lqs2
 * @date 2018/10/20, Sat
 */
@Data
public class StrokeData {
    private int startX;
    private int startY;
    private int[][] data;
}
