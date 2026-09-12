package me.mioclient.manager;

import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.world.HoleData;

import me.mioclient.api.ClientAccessor;
import me.mioclient.util.world.HoleData;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.event.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/HoleTracker.class */
public final class HoleTracker implements ClientAccessor {
    public List<HoleData> list = new ArrayList();
    public boolean flag;
    public Future<?> future;

    public HoleTracker() {
        eventBus.register(this);
        this.future = executorService.submit(new HoleScanTask(this));
    }

    public boolean isHoleAt(BlockPos blockPos) {
        return this.list.stream().anyMatch(holeSnapData -> {
            return holeSnapData.getBox().intersects(new Box(blockPos));
        });
    }

    public static HoleData detectHole(BlockPos blockPos, Direction direction) {
        HoleData holeSnapData2724;
        if (isSolidBlock(blockPos) || !isSolidBlock(blockPos.down()) || isSolidBlock(blockPos.up())) {
            return null;
        }
        if (minecraftClient.world.getBlockState(blockPos.down()).isOf(Blocks.END_PORTAL)) {
            return null;
        }
        boolean isSolidBlock = isSolidBlock(blockPos.up(2));
        HoleData.HoleSnapDataMode holeSnapDataMode = HoleData.HoleSnapDataMode.SAFE;
        Direction direction2 = null;
        for (Direction direction3 : Direction.values()) {
            if (direction != direction3.getOpposite() && direction3 != Direction.UP) {
                BlockPos offset = blockPos.offset(direction3);
                Block block = minecraftClient.world.getBlockState(offset).getBlock();
                if (isSolidBlock(offset)) {
                    if (block == Blocks.RESPAWN_ANCHOR) {
                        return null;
                    }
                    if ((block.getBlastResistance() < Float.intBitsToFloat(1142292480) && block.getBlastResistance() >= 0.0f) || block.getHardness() == 0.0f) {
                        return null;
                    }
                    if (block.getBlastResistance() >= Float.intBitsToFloat(1142292480) && block.getHardness() >= 0.0f) {
                        holeSnapDataMode = HoleData.HoleSnapDataMode.UNSAFE;
                    }
                } else {
                    if (direction2 != null || direction != null || (holeSnapData2724 = detectHole(blockPos.offset(direction3), direction3)) == null) {
                        return null;
                    }
                    if (!holeSnapData2724.isTrapped()) {
                        isSolidBlock = false;
                    }
                    if (holeSnapDataMode == HoleData.HoleSnapDataMode.SAFE) {
                        holeSnapDataMode = holeSnapData2724.getHoleSnapDataMode();
                    }
                    direction2 = direction3;
                }
            }
        }
        return new HoleData(holeSnapDataMode, blockPos.toImmutable(), direction2 == null ? new Box(blockPos) : new Box(blockPos).stretch(direction2.getVector().getX(), 0.0d, direction2.getVector().getZ()), isSolidBlock);
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (this.future.isDone() || this.future.isCancelled()) {
            this.future = executorService.submit(new HoleScanTask(this));
        }
        this.flag = isHoleAt(PlayerUtils.getRoundedBlockPos());
    }

    public static boolean isSolidBlock(BlockPos blockPos) {
        BlockState blockState = minecraftClient.world.getBlockState(blockPos);
        return (blockState.isAir() || blockState.isOf(Blocks.FIRE) || blockState.isOf(Blocks.SOUL_FIRE) || (blockState.getBlock() instanceof ButtonBlock) || (blockState.getBlock() instanceof TorchBlock) || (blockState.getBlock() instanceof RailBlock) || blockState.isOf(Blocks.LIGHT)) ? false : true;
    }

    public List<HoleData> getHoles() {
        return this.list;
    }

    public void setHoles(List<HoleData> list) {
        this.list = list;
    }

    public boolean isInHole() {
        return this.flag;
    }
}
