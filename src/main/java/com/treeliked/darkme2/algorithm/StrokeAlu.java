package com.treeliked.darkme2.algorithm;

import com.treeliked.darkme2.model.stroke.Block;
import com.treeliked.darkme2.model.stroke.CheckerBoard;
import com.treeliked.darkme2.model.stroke.Loc;

import java.util.Stack;

/**
 * 一笔画算法
 *
 * @author lqs2
 * @date 2018/10/18, Thu
 */

class StrokeAlu {


    /**
     * 计算超时时间，单位ms, 2min
     */
    private static final int TIME_OUT_TIME = 120000;
    /**
     * 一笔画布局
     */
    private CheckerBoard board;

    /**
     * 用来保存步骤，-1表示不需要走，0表示需要但未走，1表示以及走过
     */
    private int[][] source;

    /**
     * 用来保存当前已经走过的步骤
     */
    private Stack<Loc> locStack;

    /**
     * 起始位置横坐标
     */
    private int startX;

    /**
     * 起始位置纵坐标
     */
    private int startY;

    /**
     * 停止计算标志
     */
    private boolean flag;

    /**
     * 开始计算开始时间
     */
    private long startTime;

    StrokeAlu(int[][] source, Stack<Loc> locStack, int startX, int startY) {
        this.source = source;
        this.locStack = locStack;
        this.startX = startX;
        this.startY = startY;
        init();
    }

    /**
     * 初始化并设置整个布局
     */
    private void init() {
        source[startX][startY] = 0;
        flag = false;
        board = new CheckerBoard(source);
        Block startBlock = new Block(new Loc(startX + 1, startY + 1), 0);
        board.setCurrentBlock(startBlock);
        locStack.push(startBlock.getLoc());
    }

    /**
     * 开始计算路径
     */
    void calPath() {
        startTime = System.currentTimeMillis();
        run();
    }


    /**
     * 核心算法，查找路径
     */
    @SuppressWarnings("Duplicates")
    private void run() {
        long curTime = System.currentTimeMillis();
        long runTime = curTime - startTime;
        if (flag || runTime > TIME_OUT_TIME) {
            locStack.empty();
            return;
        }
        Block currentBlock;
        // 向上
        currentBlock = board.getCurrentBlock();
        Block upBlock = board.getBlock(currentBlock, Loc.UP);
        if (upBlock != null && upBlock.getStatus() != 0) {
            upBlock.setStatus(0);
            locStack.push(upBlock.getLoc());
            board.setCurrentBlock(upBlock);
            run();
            if (flag) {
                return;
            }
        }
        // 向右
        currentBlock = board.getCurrentBlock();
        Block rightBlock = board.getBlock(currentBlock, Loc.RIGHT);
        if (rightBlock != null && rightBlock.getStatus() != 0) {
            rightBlock.setStatus(0);
            locStack.push(rightBlock.getLoc());
            board.setCurrentBlock(rightBlock);
            run();
            if (flag) {
                return;
            }
        }
        // 向下
        currentBlock = board.getCurrentBlock();
        Block downBlock = board.getBlock(currentBlock, Loc.DOWN);
        if (downBlock != null && downBlock.getStatus() != 0) {
            downBlock.setStatus(0);
            locStack.push(downBlock.getLoc());
            board.setCurrentBlock(downBlock);
            run();
            if (flag) {
                return;
            }
        }
        // 向左
        currentBlock = board.getCurrentBlock();
        Block leftBlock = board.getBlock(currentBlock, Loc.LEFT);
        if (leftBlock != null && leftBlock.getStatus() != 0) {
            leftBlock.setStatus(0);
            locStack.push(leftBlock.getLoc());
            board.setCurrentBlock(leftBlock);
            run();
            if (flag) {
                return;
            }
        }
        // 找到路径
        if (locStack.size() - 1 == board.getStepTotal()) {
            System.out.println("找到路径：");
            flag = true;
            outputStackData();
            return;
        }

        // 当前步骤往下无解
        currentBlock.setStatus(1);
        locStack.pop();

        // 所有step出栈，无解
        if (locStack.size() == 0) {
            locStack.empty();
            flag = true;
            return;
        }
        board.setCurrentBlock(board.getBlock(locStack.peek()));
    }

    private void outputStackData() {
        for (Loc l : locStack) {
            System.out.print(String.format("(%d, %d)-> ", l.getX() - 1, l.getY() - 1));
        }
        System.out.println();
    }

}