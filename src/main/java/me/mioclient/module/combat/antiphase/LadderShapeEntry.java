package me.mioclient.module.combat.antiphase;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/LadderShapeEntry.class */
public final class LadderShapeEntry {
    public final Direction direction;
    public final Box box;

    public LadderShapeEntry(Direction direction, Box box) {
        this.direction = direction;
        this.box = box;
    }

    public static LadderShapeEntry fromShape(Direction direction, VoxelShape voxelShape) {
        return new LadderShapeEntry(direction.getOpposite(), voxelShape.getBoundingBox());
    }




    public Direction getDirection() {
        return this.direction;
    }

    public Box getBox() {
        return this.box;
    }
}
