package me.mioclient.render.shape;

import me.mioclient.util.math.ColorUtils;

import me.mioclient.util.math.ColorUtils;

import java.awt.Color;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import me.mioclient.manager.RotationManager;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/FadingBoxRenderer.class */
public class FadingBoxRenderer {
    public Box box;
    public long num;
    public float val = Float.intBitsToFloat(1065353216);

    public void startFadeOut() {
        this.num = System.currentTimeMillis() + 150;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.box = new Box(blockPos);
    }

    public void setBoundingBox(Box box) {
        this.box = box;
    }

    public void setStrokeWidth(float f) {
        this.val = f;
    }

    public void renderFadingBox(MatrixStack matrixStack, Color color, Color color2, float f, boolean z) {
        if (this.box == null || !RotationManager.isInFrustum(this.box)) {
            return;
        }
        float f2 = getFadeProgress(f);
        BoxRenderer.drawFilledBox(matrixStack, this.box, z ? ColorUtils.withAlpha(color, (int) (color.getAlpha() * f2)) : color);
        BoxRenderer.drawBoxOutline(matrixStack, this.box, z ? ColorUtils.withAlpha(color2, (int) (color2.getAlpha() * f2)) : color2, this.val);
        if (f2 == 0.0f) {
            this.box = null;
        }
    }

    public float getFadeProgress(float f) {
        return MathHelper.clamp(Float.intBitsToFloat(1065353216) - (((float) Math.max(System.currentTimeMillis() - this.num, 0L)) / f), 0.0f, Float.intBitsToFloat(1065353216));
    }

    public Box getBox() {
        return this.box;
    }
}
