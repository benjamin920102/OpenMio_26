package me.mioclient.module.combat.automine;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.mode.player.AutoCraftMode;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.module.combat.AutoMine;
import me.mioclient.module.player.SpeedMine;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import me.mioclient.mode.player.AutoCraftMode;
import me.mioclient.util.world.WorldUtils;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/MineStrategyBase.class */
public abstract class MineStrategyBase implements ClientAccessor, MineTargetStrategy {
    public static SpeedMine speedmine = (SpeedMine) ClientServices.moduleInjector.getModule(SpeedMine.class);
    public final AutoMine autoMine;

    public MineStrategyBase(AutoMine autoMine) {
        this.autoMine = autoMine;
    }

    public boolean isValidMineTarget(BlockPos blockPos) {
        if (blockPos.equals(this.autoMine.speedMineHelper.getRecentBlockPos())) {
            return false;
        }
        if (minecraftClient.player.getEyePos().distanceTo(blockPos.toCenterPos()) > this.autoMine.getRange() || !speedmine.isBlockAllowed(blockPos)) {
            return false;
        }
        if (this.autoMine.strictDirection.getValue().booleanValue() && BlockPlacer.getInteractableSides(blockPos).isEmpty()) {
            return false;
        }
        if (!this.autoMine.raytrace.getValue().booleanValue() || WorldUtils.isAnyPositionVisible(AutoCraftMode.X8.getWorldCorners(blockPos))) {
            return WorldUtils.isBreakable(blockPos);
        }
        return false;
    }

    public boolean isSpeedMineTarget(BlockPos blockPos) {
        if (blockPos == null) {
            return false;
        }
        return blockPos.equals(speedmine.getMineTargetPos()) || blockPos.equals(speedmine.getRebreakTargetPos());
    }

    public boolean isSpaceBelowEmpty(Entity entity) {
        return minecraftClient.world.isSpaceEmpty(entity.getBoundingBox().stretch(0.0d, Double.longBitsToDouble(-4631501856787818086L), 0.0d));
    }

    public boolean isEnabled() {
        return false;
    }
}
