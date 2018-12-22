package com.treeliked.darkme2.model.stroke;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 小格子
 *
 * @author lqs2
 * @date 2018/10/18, Thu
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Block {
    private Loc loc;
    private int status;
}
