package com.treeliked.darkme2.algorithm;

import com.treeliked.darkme2.model.stroke.Loc;

import java.util.Stack;

/**
 * 算法集合
 *
 * @author lqs2
 * @date 2018/10/20, Sat
 */
public class Alus {

    public static void strokeCal(Stack<Loc> locStack, int startX, int startY, int[][] source) {
        StrokeAlu alu = new StrokeAlu(source, locStack, startX, startY);
        alu.calPath();
    }
}
