package me.mioclient.mode.render;

import me.mioclient.util.math.GridPoint;

public enum Direction {
    UP(0, -1),
    RIGHT(1, 0),
    DOWN(0, 1),
    LEFT(-1, 0);

    public final GridPoint positionData;

    Direction(int i, int i2) {
        this.positionData = new GridPoint(i, i2);
    }

    public GridPoint getPositionData244() {
        return this.positionData;
    }

    public Direction getOpposite() {
        switch (this) {
            case UP:
                return DOWN;
            case RIGHT:
                return LEFT;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
    }
}
