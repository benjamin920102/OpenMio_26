package me.mioclient.render.hud;

import me.mioclient.api.ClientServices;
import me.mioclient.module.HudModule;

import me.mioclient.gui.screen.HudEditorScreen;
import me.mioclient.mode.client.ModuleListMode;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shape.Shape2DRenderer;
import me.mioclient.util.anim.ValueAnimator;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.math.Rectangle;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.gui.GuiComponent;
import me.mioclient.gui.screen.HudEditorScreen;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.math.Rectangle;
import me.mioclient.util.anim.ValueAnimator;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import me.mioclient.module.client.Fonts;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import me.mioclient.module.HudModule;
import me.mioclient.mode.client.ModuleListMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/DraggableHudElement.class */
public abstract class DraggableHudElement implements ClientAccessor, GuiComponent {
    public static final Color color = new Color(0, 0, 0, 120);
    public static boolean flag = false;
    public final HudModule moduleList;
    public final ValueAnimator zoomHelper = new ValueAnimator();
    public final ValueAnimator zoomHelper2 = new ValueAnimator();
    public ModuleListMode moduleListMode = ModuleListMode.NONE;
    public float val = Float.intBitsToFloat(1056964608);
    public float val2 = Float.intBitsToFloat(1056964608);
    public int num;
    public int num2;
    public float[] floatArr;
    public boolean flag2;
    public long num3;

    public DraggableHudElement(HudModule moduleList) {
        this.moduleList = moduleList;
    }

    @Override // me.mioclient.gui.GuiComponent
    public void render(DrawContext drawContext, MatrixStack matrixStack, double d, double d2) {
        float[] floatArray2950 = getMeasuredSize();
        if (!containsPoint(d, d2) || this.flag2) {
            this.num3 = -1L;
        } else {
            if (this.num3 == -1) {
                this.num3 = System.currentTimeMillis();
            }
            if (this.num3 + 300 <= System.currentTimeMillis()) {
                HudEditorScreen.helper_22.initialize(this.moduleList.getName(), getHorizontalSide() == 1 ? getX() + floatArray2950[0] + Float.intBitsToFloat(1073741824) : (getX() - Float.intBitsToFloat(1073741824)) - ClientTextRenderer.INSTANCE.getStringWidth(this.moduleList.getName()), getY(), getHorizontalSide());
            }
        }
        if (flag) {
            return;
        }
        float f = getX();
        float f2 = getY();
        Shape2DRenderer.drawRect(matrixStack, f, f2, f + Math.max(floatArray2950[0], Float.intBitsToFloat(1082130432)), f2 + Math.max(floatArray2950[1], Float.intBitsToFloat(1082130432)), ColorUtils.toIntWithAlphaPercent(color, ((ClientServices.getHud().getAlpha() * RenderSystem.getShaderColor()[3]) * color.getAlpha()) / Float.intBitsToFloat(1132396544)));
        float f3 = this.zoomHelper.getValue();
        if (f3 > 0.0f) {
            Shape2DRenderer.drawOutline(matrixStack, f - Float.intBitsToFloat(1065353216), f2 - Float.intBitsToFloat(1065353216), f + Math.max(floatArray2950[0], Float.intBitsToFloat(1082130432)), f2 + Math.max(floatArray2950[1], Float.intBitsToFloat(1082130432)), ColorUtils.withAlpha(ColorUtils.getThemeColor(), (int) (f3 * Float.intBitsToFloat(1132396544))));
        }
    }

    @Override // me.mioclient.gui.GuiComponent
    public boolean containsPoint(double d, double d2) {
        float[] floatArray2950 = getMeasuredSize();
        return d >= ((double) getX()) && d <= ((double) (getX() + Math.max(floatArray2950[0], Float.intBitsToFloat(1082130432)))) && d2 >= ((double) getY()) && d2 <= ((double) (getY() + Math.max(floatArray2950[1], Float.intBitsToFloat(1082130432))));
    }

    public void updateVisibilityAnim() {
        this.zoomHelper2.animateTo(isVisible() ? Float.intBitsToFloat(1065353216) : 0.0f, 250L);
    }

    public abstract float[] getSize();

    public float getX() {
        return Math.min(this.val * minecraftClient.getWindow().getScaledWidth(), minecraftClient.getWindow().getScaledWidth() - getMeasuredSize()[0]);
    }

    public float getRelativeX() {
        return this.val;
    }

    public void setRelativeX(float f, boolean z) {
        if (z) {
            this.val = f / minecraftClient.getWindow().getScaledWidth();
        } else {
            this.val = MathHelper.clamp(f, 0.0f, Float.intBitsToFloat(1065353216));
        }
    }

    public float getY() {
        return Math.min(this.val2 * minecraftClient.getWindow().getScaledHeight(), minecraftClient.getWindow().getScaledHeight() - getMeasuredSize()[1]);
    }

    public float getRelativeY() {
        return this.val2;
    }

    public void setRelativeY(float f, boolean z) {
        if (z) {
            this.val2 = f / minecraftClient.getWindow().getScaledHeight();
        } else {
            this.val2 = MathHelper.clamp(f, 0.0f, Float.intBitsToFloat(1065353216));
        }
    }

    @Override // me.mioclient.gui.GuiComponent
    public int getFontHeight() {
        int i = ClientTextRenderer.INSTANCE.getFontHeight();
        if (Fonts.fonts.isToggled()) {
            i--;
        }
        return i;
    }

    public void setDragging(boolean z) {
        if (this.flag2 == z) {
            return;
        }
        this.zoomHelper.animateTo(z ? Float.intBitsToFloat(1065353216) : 0.0f, 250L);
        this.flag2 = z;
    }

    public boolean isDragging() {
        return this.flag2;
    }

    public float[] getMeasuredSize() {
        return this.floatArr;
    }

    public void setMeasuredSize(float[] fArr) {
        this.floatArr = fArr;
    }

    public ModuleListMode getAnchor() {
        return this.moduleListMode;
    }

    public void setAnchor(ModuleListMode moduleListMode) {
        this.moduleListMode = moduleListMode;
    }

    public boolean isVisible() {
        return this.moduleList.isToggled();
    }

    public float getAlignedX(float f) {
        return this.moduleListMode == ModuleListMode.TOP_CENTER ? (getX() + (this.floatArr[0] / Float.intBitsToFloat(1073741824))) - (f / Float.intBitsToFloat(1073741824)) : getHorizontalSide() == -1 ? (getX() + this.floatArr[0]) - f : getX();
    }

    public float getAlignedY(float f) {
        return getVerticalSide() == -1 ? (getY() + this.floatArr[1]) - f : getY();
    }

    public int getHorizontalSide() {
        if (this.num == 0) {
            computeSides();
        }
        return this.num;
    }

    public int getVerticalSide() {
        if (this.num2 == 0) {
            computeSides();
        }
        return this.num2;
    }

    public void computeSides() {
        this.num = getX() + (this.floatArr[0] / Float.intBitsToFloat(1073741824)) > ((float) minecraftClient.getWindow().getScaledWidth()) / Float.intBitsToFloat(1073741824) ? -1 : 1;
        this.num2 = getY() + (this.floatArr[1] / Float.intBitsToFloat(1073741824)) > ((float) minecraftClient.getWindow().getScaledHeight()) / Float.intBitsToFloat(1073741824) ? -1 : 1;
    }

    public Rectangle getData_32936() {
        return Rectangle.fromPositionAndSize(this.val * minecraftClient.getWindow().getScaledWidth(), this.val2 * minecraftClient.getWindow().getScaledHeight(), this.floatArr[0], this.floatArr[1]);
    }

    public HudModule getHudModule() {
        return this.moduleList;
    }

    public float get2142() {
        return this.zoomHelper2.getValue();
    }
}
