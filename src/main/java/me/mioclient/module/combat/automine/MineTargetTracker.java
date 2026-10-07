package me.mioclient.module.combat.automine;

import me.mioclient.api.ClientServices;

import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientAccessor;
import me.mioclient.module.player.speedmine.SpeedMineTarget;

import me.mioclient.module.combat.AutoMine;
import me.mioclient.module.player.SpeedMine;
import net.minecraft.core.BlockPos;

public final class MineTargetTracker implements ClientAccessor {
    /* 26.2 port: resolved lazily — eager static init ran before the module registry was ready, leaving speedmine null. */
    private static SpeedMine speedmine;

    public static SpeedMine getSpeedmine() {
        if (speedmine == null && ClientServices.moduleInjector != null) {
            try {
                speedmine = (SpeedMine) ClientServices.moduleInjector.getModule(SpeedMine.class);
            } catch (Exception e) {
            }
        }
        return speedmine;
    }
    public final Stopwatch stopwatch = new Stopwatch();
    public final AutoMine autoMine;
    public BlockPos blockPos;

    public MineTargetTracker(AutoMine autoMine) {
        this.autoMine = autoMine;
    }

    public boolean hasActiveMiningTarget() {
        BlockPos blockPos1226 = getRecentBlockPos();
        SpeedMine speedmine_lcl = getSpeedmine();
        return ((blockPos1226 == null || minecraftClient.level.isEmptyBlock(blockPos1226)) && (speedmine_lcl == null || speedmine_lcl.getSearchTarget() == null)) ? false : true;
    }

    public BlockPos getBlockPos() {
        SpeedMine speedmine_lcl = getSpeedmine();
        if (speedmine_lcl == null) {
            return null;
        }
        SpeedMineTarget speedMineSearchHelper41059 = speedmine_lcl.getSearchTarget();
        if (speedMineSearchHelper41059 == null) {
            return null;
        }
        return speedMineSearchHelper41059.getBlockPos();
    }

    public BlockPos getRecentBlockPos() {
        if (this.stopwatch.hasElapsedMs(200L)) {
            return null;
        }
        return this.blockPos;
    }

    public void do1227(BlockPos blockPos) {
        this.blockPos = blockPos;
        this.stopwatch.reset();
    }

    public boolean is1228(long j) {
        return this.stopwatch.hasElapsedMs(j);
    }
}