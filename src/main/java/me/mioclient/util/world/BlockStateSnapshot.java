package me.mioclient.util.world;

import me.mioclient.api.ClientAccessor;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

public final class BlockStateSnapshot implements ClientAccessor {
    public final BlockState blockState;
    public final BlockPos blockPos;
    public final long num;

    public BlockStateSnapshot(BlockState blockState, BlockPos blockPos, long j) {
        this.blockState = blockState;
        this.blockPos = blockPos;
        this.num = j;
    }

    public static BlockStateSnapshot getBlockStateSearchHelper42779(BlockPos blockPos) {
        return new BlockStateSnapshot(minecraftClient.level.getBlockState(blockPos), blockPos, System.currentTimeMillis());
    }

    public boolean isInvalid() {
        return System.currentTimeMillis() - this.num >= 500;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public long getTimestampMs() {
        return this.num;
    }
}
