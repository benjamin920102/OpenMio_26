package me.mioclient.event.events.render;

import me.mioclient.event.Event;

import net.minecraft.world.level.block.state.BlockState;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;

public class DrawBlockOutlineEvent extends Event {
    public PoseStack matrixStack;
    public VertexConsumer vertexConsumer;
    public BlockPos blockPos;
    public BlockState blockState;

    public DrawBlockOutlineEvent(PoseStack matrixStack, VertexConsumer vertexConsumer, BlockPos blockPos, BlockState blockState) {
        this.matrixStack = matrixStack;
        this.vertexConsumer = vertexConsumer;
        this.blockPos = blockPos;
        this.blockState = blockState;
    }

    public PoseStack getMatrixStack() {
        return this.matrixStack;
    }

    public void setMatrixStack(PoseStack matrixStack) {
        this.matrixStack = matrixStack;
    }

    public VertexConsumer getVertexConsumer() {
        return this.vertexConsumer;
    }

    public void setVertexConsumer(VertexConsumer vertexConsumer) {
        this.vertexConsumer = vertexConsumer;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }
}
