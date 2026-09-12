package me.mioclient.module.combat.antiphase;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AntiPhasePlacement.class */
public final class AntiPhasePlacement {
    public final BlockPos blockPos;
    public final Direction direction;
    public final Box box;

    public AntiPhasePlacement(BlockPos blockPos, Direction direction, Box box) {
        this.blockPos = blockPos;
        this.direction = direction;
        this.box = box;
    }

    public Vec3d getFaceCenter() {
        Vec3d centerPos = this.blockPos.toCenterPos();
        return centerPos.offset(this.direction, Double.longBitsToDouble(4602678819172646912L));
    }




    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Box getBox() {
        return this.box;
    }
}
