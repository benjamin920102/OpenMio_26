package me.mioclient.module.combat.anchoraura;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AnchorPlacement.class */
public final class AnchorPlacement {
    public final BlockPos blockPos;
    public final Direction direction;
    public final AnchorTarget autoCrystalData_2;

    public AnchorPlacement(BlockPos blockPos, Direction direction, AnchorTarget autoCrystalData_2) {
        this.blockPos = blockPos;
        this.direction = direction;
        this.autoCrystalData_2 = autoCrystalData_2;
    }

    public AnchorPlacement preferOver(AnchorPlacement anchorAuraData) {
        if (anchorAuraData != null && anchorAuraData.autoCrystalData_2.getDamage() > this.autoCrystalData_2.getDamage()) {
            return anchorAuraData;
        }
        return this;
    }




    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public AnchorTarget getTarget() {
        return this.autoCrystalData_2;
    }
}
