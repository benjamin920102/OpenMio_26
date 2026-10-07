package me.mioclient.event.events.world;

import me.mioclient.event.Event;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockCollisionShapeEvent extends Event {
    public static final BlockCollisionShapeEvent voxelShapeEvent = new BlockCollisionShapeEvent();
    public VoxelShape voxelShape;
    public BlockPos blockPos;
    public BlockState blockState;

    public static BlockCollisionShapeEvent create(VoxelShape voxelShape, BlockPos blockPos, BlockState blockState) {
        voxelShapeEvent.setShape(voxelShape);
        voxelShapeEvent.setBlockPos(blockPos);
        voxelShapeEvent.setBlockState(blockState);
        return voxelShapeEvent;
    }

    public void setShape(VoxelShape voxelShape) {
        this.voxelShape = voxelShape;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    public VoxelShape getShape() {
        return this.voxelShape;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }
}
