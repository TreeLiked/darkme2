package com.treeliked.darkme2.model.stroke;

import lombok.Data;

/**
 * 游戏布局
 *
 * @author lqs2
 * @date 2018/10/18, Thu
 */

@Data
public class CheckerBoard {

    private Block[][] blocks;
    private Block currentBlock;
    private int stepTotal;
    private int x;
    private int y;

    public CheckerBoard(int[][] arr) {
        int i = arr.length;
        int j = arr[0].length;
        x = i + 2;
        y = j + 2;
        blocks = new Block[x][y];
        for (int m = 0; m < x; m++) {
            for (int n = 0; n < y; n++) {
                if (m == 0 || n == 0 || m == x - 1 || n == y - 1) {
                    blocks[m][n] = null;
                } else {
                    blocks[m][n] = new Block(new Loc(m, n), arr[m - 1][n - 1]);
                    if (arr[m - 1][n - 1] == 1) {
                        stepTotal++;
                    }
                }
            }
        }
//        for (Block[] blockLine : blocks) {
//            for (Block block : blockLine) {
//                if (block != null) {
//                    System.out.print(block.getLoc().getX() + "" + block.getLoc().getY() + "-" + block.getStatus() + "\t");
//                } else {
//                    System.out.print("null\t");
//                }
//            }
//            System.out.println();
//        }
    }

    public Block getBlock(Loc loc) {
        return blocks[loc.getX()][loc.getY()];
    }


    public Block getBlock(Block block, int direction) {
        int x = block.getLoc().getX();
        int y = block.getLoc().getY();
        Loc loc = new Loc(x, y);
        switch (direction) {
            case Loc.UP:
                loc.setX(x - 1);
                break;
            case Loc.DOWN:
                loc.setX(x + 1);
                break;
            case Loc.LEFT:
                loc.setY(y - 1);
                break;
            case Loc.RIGHT:
                loc.setY(y + 1);
            default:
                break;
        }
        return getBlock(loc);
    }

    public void outputBlockInfo(Block block) {
        System.out.println(block);
    }
}
