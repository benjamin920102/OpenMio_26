package me.mioclient.module.player.speedmine;

import me.mioclient.util.world.WorldUtils;

import me.mioclient.render.shape.BoxRenderer;

import me.mioclient.api.ClientAccessor;
import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.render.RenderUtil;

import java.awt.Color;
import me.mioclient.module.player.SpeedMine;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.util.world.WorldUtils;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SpeedMineTarget.class */
public final class SpeedMineTarget implements ClientAccessor {
    public final BlockPos blockPos;
    public float val;
    public float val2;

    public SpeedMineTarget(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public void do2141(SpeedMine speedMine, MatrixStackEvent matrixStackEvent) {
        VoxelShape outlineShape = minecraftClient.world.getBlockState(this.blockPos).getOutlineShape(minecraftClient.world, this.blockPos);
        if (outlineShape.isEmpty() || !WorldUtils.isBreakable(this.blockPos)) {
            return;
        }
        float f = get2142();
        Color[] colorArray1773 = speedMine.colorMode.getValue().getRenderColors(speedMine, f);
        Box offset = speedMine.renderMode.getValue().getRenderBox(speedMine, outlineShape.getBoundingBox(), f).offset(this.blockPos);
        BoxRenderer.drawFilledBox(matrixStackEvent.getMatrixStack(), offset, colorArray1773[0]);
        BoxRenderer.drawBoxOutline(matrixStackEvent.getMatrixStack(), offset, colorArray1773[1], speedMine.lineWidth.getValue().floatValue());
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public float get2142() {
        return MathHelper.clamp(MathHelper.lerp(RenderUtil.getPartialTick(), this.val, this.val2), 0.0f, Float.intBitsToFloat(1065353216));
    }

    public void addMiningProgress(float f) {
        setMiningProgress(this.val2 + f);
    }

    public void setMiningProgress(float f) {
        this.val = this.val2;
        this.val2 = f;
    }
}
