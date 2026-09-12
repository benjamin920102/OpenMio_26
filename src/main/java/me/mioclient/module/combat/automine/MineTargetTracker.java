package me.mioclient.module.combat.automine;

import me.mioclient.api.ClientServices;

import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.module.player.speedmine.SpeedMineTarget;

import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.combat.AutoMine;
import me.mioclient.module.player.SpeedMine;
import net.minecraft.util.math.BlockPos;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/MineTargetTracker.class */
public final class MineTargetTracker implements ClientAccessor {
    public static SpeedMine speedmine = (SpeedMine) ClientServices.moduleInjector.getModule(SpeedMine.class);
    public final Stopwatch stopwatch = new Stopwatch();
    public final AutoMine autoMine;
    public BlockPos blockPos;

    public MineTargetTracker(AutoMine autoMine) {
        this.autoMine = autoMine;
    }

    public boolean hasActiveMiningTarget() {
        BlockPos blockPos1226 = getRecentBlockPos();
        return ((blockPos1226 == null || minecraftClient.world.isAir(blockPos1226)) && speedmine.getSearchTarget() == null) ? false : true;
    }

    public BlockPos getBlockPos() {
        SpeedMineTarget speedMineSearchHelper41059 = speedmine.getSearchTarget();
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
