package me.mioclient.event.events.render;

import me.mioclient.event.Event;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/DrawBlockOutlineEvent.class */
public class DrawBlockOutlineEvent extends Event {
    public MatrixStack matrixStack;
    public VertexConsumer vertexConsumer;
    public BlockPos blockPos;
    public BlockState blockState;

    public DrawBlockOutlineEvent(MatrixStack matrixStack, VertexConsumer vertexConsumer, BlockPos blockPos, BlockState blockState) {
        this.matrixStack = matrixStack;
        this.vertexConsumer = vertexConsumer;
        this.blockPos = blockPos;
        this.blockState = blockState;
    }

    public MatrixStack getMatrixStack() {
        return this.matrixStack;
    }

    public void setMatrixStack(MatrixStack matrixStack) {
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
