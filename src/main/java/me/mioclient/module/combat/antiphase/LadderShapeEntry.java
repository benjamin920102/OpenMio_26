package me.mioclient.module.combat.antiphase;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class LadderShapeEntry {
    public final Direction direction;
    public final AABB box;

    public LadderShapeEntry(Direction direction, AABB box) {
        this.direction = direction;
        this.box = box;
    }

    public static LadderShapeEntry fromShape(Direction direction, VoxelShape voxelShape) {
        return new LadderShapeEntry(direction.getOpposite(), voxelShape.bounds());
    }

    public Direction getDirection() {
        return this.direction;
    }

    public AABB getBox() {
        return this.box;
    }
}