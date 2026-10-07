package me.mioclient.util.world;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public final class HoleData {
    public final HoleSnapDataMode holeSnapDataMode;
    public final BlockPos blockPos;
    public final AABB box;
    public final boolean flag;

    
    public enum HoleSnapDataMode {
        SAFE,
        UNSAFE
    }

    public HoleData(HoleSnapDataMode holeSnapDataMode, BlockPos blockPos, AABB box, boolean z) {
        this.holeSnapDataMode = holeSnapDataMode;
        this.blockPos = blockPos;
        this.box = box;
        this.flag = z;
    }

    public HoleSnapDataMode getHoleSnapDataMode() {
        return this.holeSnapDataMode;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public AABB getBox() {
        return this.box;
    }

    public boolean isTrapped() {
        return this.flag;
    }
}
