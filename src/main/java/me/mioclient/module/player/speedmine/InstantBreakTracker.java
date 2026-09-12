package me.mioclient.module.player.speedmine;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientServices;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.player.SpeedMine;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import me.mioclient.util.world.WorldUtils;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/InstantBreakTracker.class */
public final class InstantBreakTracker {
    public static final long num = 750;
    public final SpeedMine speedMine;
    public final Stopwatch stopwatch = new Stopwatch();
    public boolean flag;
    public BlockPos blockPos;

    public InstantBreakTracker(SpeedMine speedMine) {
        this.speedMine = speedMine;
    }

    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        BlockUpdateS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof BlockUpdateS2CPacket ? (BlockUpdateS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof BlockUpdateS2CPacket) {
            BlockUpdateS2CPacket blockUpdateS2CPacket = packet904;
            if (blockUpdateS2CPacket.getState().isAir() && blockUpdateS2CPacket.getPos().equals(this.blockPos)) {
                reset();
            }
        }
        ChunkDeltaUpdateS2CPacket packet9042 = (channelRead0Event.getPacket()) instanceof ChunkDeltaUpdateS2CPacket ? (ChunkDeltaUpdateS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet9042 instanceof ChunkDeltaUpdateS2CPacket) {
            packet9042.visitUpdates((blockPos, blockState) -> {
                if (blockPos.equals(this.blockPos) && blockState.isAir()) {
                    reset();
                }
            });
        }
    }

    public void beginTracking(BlockPos blockPos) {
        if (this.flag) {
            return;
        }
        this.flag = true;
        this.blockPos = blockPos;
        this.stopwatch.reset();
    }

    public void recalculateLayout() {
        if (this.flag) {
            if (!WorldUtils.isBreakable(this.blockPos) || WorldUtils.isAir(this.blockPos)) {
                reset();
            }
            if (this.blockPos == null || this.blockPos.equals(this.speedMine.getMineTargetPos())) {
                return;
            }
            reset();
        }
    }

    public boolean isInstantBreakReady() {
        if (!this.flag || this.blockPos == null) {
            return false;
        }
        long max = Math.max((long) (Math.ceil((ClientServices.holeSnapRenderHelper.getPing() * Float.intBitsToFloat(1097859072)) / Float.intBitsToFloat(1112014848)) * Double.longBitsToDouble(4632233691727265792L)), 750L);
        if (this.speedMine.tPSSync.getValue().booleanValue()) {
            max = (long) (((float) max) / ClientServices.holeSnapRenderHelper.getTickRateFactor());
        }
        return this.stopwatch.hasElapsedMs((long) (((float) max) + this.speedMine.instantDelay.getValue().floatValue()));
    }

    public void reset() {
        this.flag = false;
        this.blockPos = null;
    }
}
