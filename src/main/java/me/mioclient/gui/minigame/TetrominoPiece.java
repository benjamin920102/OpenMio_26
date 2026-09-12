package me.mioclient.gui.minigame;

import java.util.Random;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/TetrominoPiece.class */
public class TetrominoPiece {
    public TetrominoShape mode_7;
    public int[][] intArr;
    public int[][][] intArr2;
    public Random random = new Random();

    public TetrominoPiece() {
        initShapeTemplates();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v3, types: [int[][], int[][][]] */
    public void initShapeTemplates() {
        this.intArr = new int[4][2];
        this.intArr2 = new int[][][]{new int[][]{new int[]{0, 0}, new int[]{0, 0}, new int[]{0, 0}, new int[]{0, 0}}, new int[][]{new int[]{0, -1}, new int[]{0, 0}, new int[]{-1, 0}, new int[]{-1, 1}}, new int[][]{new int[]{0, -1}, new int[]{0, 0}, new int[]{1, 0}, new int[]{1, 1}}, new int[][]{new int[]{0, -1}, new int[]{0, 0}, new int[]{0, 1}, new int[]{0, 2}}, new int[][]{new int[]{-1, 0}, new int[]{0, 0}, new int[]{1, 0}, new int[]{0, 1}}, new int[][]{new int[]{0, 0}, new int[]{1, 0}, new int[]{0, 1}, new int[]{1, 1}}, new int[][]{new int[]{-1, -1}, new int[]{0, -1}, new int[]{0, 0}, new int[]{0, 1}}, new int[][]{new int[]{1, -1}, new int[]{0, -1}, new int[]{0, 0}, new int[]{0, 1}}};
        setShape(TetrominoShape.NoShape);
    }

    public void setShape(TetrominoShape mode_7) {
        for (int i = 0; i < 4; i++) {
            System.arraycopy(this.intArr2[mode_7.ordinal()][i], 0, this.intArr[i], 0, 2);
        }
        this.mode_7 = mode_7;
    }

    public void setBlockX(int i, int i2) {
        this.intArr[i][0] = i2;
    }

    public void setBlockY(int i, int i2) {
        this.intArr[i][1] = i2;
    }

    public int getBlockX(int i) {
        return this.intArr[i][0];
    }

    public int getBlockY(int i) {
        return this.intArr[i][1];
    }

    public TetrominoShape getShape() {
        return this.mode_7;
    }

    public void randomizeShape() {
        setShape(TetrominoShape.values()[(Math.abs(this.random.nextInt()) % 7) + 1]);
    }

    public int getMinX() {
        int i = this.intArr[0][0];
        for (int i2 = 0; i2 < 4; i2++) {
            i = Math.min(i, this.intArr[i2][0]);
        }
        return i;
    }

    public int getMinY() {
        int i = this.intArr[0][1];
        for (int i2 = 0; i2 < 4; i2++) {
            i = Math.min(i, this.intArr[i2][1]);
        }
        return i;
    }

    public TetrominoPiece rotateClockwise() {
        if (this.mode_7 == TetrominoShape.SquareShape) {
            return this;
        }
        TetrominoPiece randomHelper = new TetrominoPiece();
        randomHelper.mode_7 = this.mode_7;
        for (int i = 0; i < 4; i++) {
            randomHelper.setBlockX(i, getBlockY(i));
            randomHelper.setBlockY(i, -getBlockX(i));
        }
        return randomHelper;
    }

    public TetrominoPiece rotateCounterClockwise() {
        if (this.mode_7 == TetrominoShape.SquareShape) {
            return this;
        }
        TetrominoPiece randomHelper = new TetrominoPiece();
        randomHelper.mode_7 = this.mode_7;
        for (int i = 0; i < 4; i++) {
            randomHelper.setBlockX(i, -getBlockY(i));
            randomHelper.setBlockY(i, getBlockX(i));
        }
        return randomHelper;
    }
}
