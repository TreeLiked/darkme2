package com.treeliked.darkme2.model.stroke;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 坐标
 *
 * @author lqs2
 * @date 2018/10/18, Thu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Loc {

    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    private int x;
    private int y;
}
