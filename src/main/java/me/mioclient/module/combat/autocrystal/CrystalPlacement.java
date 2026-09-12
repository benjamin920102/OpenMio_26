package me.mioclient.module.combat.autocrystal;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/CrystalPlacement.class */
public final class CrystalPlacement {
    public final BlockPos blockPos;
    public final PlayerEntity playerEntity;
    public final double val;
    public final AutoCrystalDataMode autoCrystalDataMode;

    public CrystalPlacement(BlockPos blockPos, PlayerEntity playerEntity, double d, AutoCrystalDataMode autoCrystalDataMode) {
        this.blockPos = blockPos;
        this.playerEntity = playerEntity;
        this.val = d;
        this.autoCrystalDataMode = autoCrystalDataMode;
    }

    public CrystalPlacement preferOver(CrystalPlacement autoCrystalData) {
        if (autoCrystalData != null && this.autoCrystalDataMode.ordinal() >= autoCrystalData.autoCrystalDataMode.ordinal()) {
            if (this.autoCrystalDataMode.ordinal() <= autoCrystalData.autoCrystalDataMode.ordinal() && autoCrystalData.val <= this.val) {
                return this;
            }
            return autoCrystalData;
        }
        return this;
    }




    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public PlayerEntity getPlayerTarget() {
        return this.playerEntity;
    }

    public double getDamage() {
        return this.val;
    }

    public AutoCrystalDataMode getDataMode() {
        return this.autoCrystalDataMode;
    }
}
