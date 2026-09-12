package me.mioclient.render.hud;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.client.ModuleListMode;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.gui.GuiComponent;

import com.mojang.blaze3d.systems.RenderSystem;
import me.mioclient.module.client.HUD;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import me.mioclient.mode.client.ModuleListMode;
import me.mioclient.mode.client.ModuleListMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ModuleListRenderer.class */
public class ModuleListRenderer implements ClientAccessor, GuiComponent {
    public static HUD hud = (HUD) ClientServices.moduleInjector.getModule(HUD.class);
    public final ArrayList<DraggableHudElement> arrayList = new ArrayList<>();
    public final ModuleListMode moduleListMode;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/SearchHelper4_10$ArrayList.class */
    public static final class ArrayList<T> extends java.util.ArrayList<T> {
        public T getLast() {
            return get(size() - 1);
        }
    }

    public ModuleListRenderer(ModuleListMode moduleListMode) {
        this.moduleListMode = moduleListMode;
    }

    @Override // me.mioclient.gui.GuiComponent
    public void render(DrawContext drawContext, MatrixStack matrixStack, double d, double d2) {
        for (int i = 0; i < this.arrayList.size(); i++) {
            DraggableHudElement moduleListSearchHelper4 = this.arrayList.get(i);
            moduleListSearchHelper4.updateVisibilityAnim();
            boolean z = moduleListSearchHelper4.isVisible() || moduleListSearchHelper4.get2142() > 0.0f;
            float[] floatArray365 = moduleListSearchHelper4.getSize();
            if (!z) {
                floatArray365 = new float[]{0.0f, 0.0f};
            }
            moduleListSearchHelper4.setMeasuredSize(floatArray365);
            if (this.moduleListMode != ModuleListMode.NONE) {
                moduleListSearchHelper4.setRelativeX(getAnchorX(floatArray365[0], this.moduleListMode), true);
                if (i == 0) {
                    moduleListSearchHelper4.setRelativeY(getAnchorY(floatArray365[1], this.moduleListMode), true);
                } else if (this.moduleListMode == ModuleListMode.BOTTOM_LEFT || this.moduleListMode == ModuleListMode.BOTTOM_RIGHT) {
                    moduleListSearchHelper4.setRelativeY(this.arrayList.get(i - 1).getY() - floatArray365[1], true);
                }
            }
            if (z) {
                RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), moduleListSearchHelper4.get2142());
                moduleListSearchHelper4.render(drawContext, matrixStack, d, d2);
                RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216));
            }
            if (i < this.arrayList.size() - 1 && this.moduleListMode != ModuleListMode.NONE && getVerticalDirection(this.moduleListMode) == 1) {
                this.arrayList.get(i + 1).setRelativeY(moduleListSearchHelper4.getY() + floatArray365[1], true);
            }
        }
    }

    public ModuleListMode getAnchor() {
        return this.moduleListMode;
    }

    public ArrayList<DraggableHudElement> getElements() {
        return this.arrayList;
    }

    public int getVerticalDirection(ModuleListMode moduleListMode) {
        switch (moduleListMode) {
            case TOP_LEFT:
            case TOP_RIGHT:
            case TOP_CENTER:
            case NONE:
                return 1;
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                return -1;
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
    }

    public float getAnchorX(float f, ModuleListMode moduleListMode) {
        switch (moduleListMode) {
            case TOP_LEFT:
            case BOTTOM_LEFT:
                return hud.getSafeAreaX();
            case TOP_RIGHT:
            case BOTTOM_RIGHT:
                return (minecraftClient.getWindow().getScaledWidth() - f) - hud.getSafeAreaX();
            case TOP_CENTER:
                return (minecraftClient.getWindow().getScaledWidth() / Float.intBitsToFloat(1073741824)) - (f / Float.intBitsToFloat(1073741824));
            case NONE:
                throw new IllegalArgumentException("dumb");
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
    }

    public float getAnchorY(float f, ModuleListMode moduleListMode) {
        switch (moduleListMode) {
            case TOP_LEFT:
            case TOP_CENTER:
                return hud.getSafeAreaY();
            case TOP_RIGHT:
                return hud.getStatusEffectOffset() + hud.getSafeAreaY();
            case NONE:
                throw new IllegalArgumentException("dumb");
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                return (minecraftClient.getWindow().getScaledHeight() - f) - getBottomPadding();
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
    }

    public float getBottomPadding() {
        return MathHelper.clamp(hud.getSafeAreaY() + hud.getBottomOffsetTarget(), 0.0f, Float.intBitsToFloat(1096810496));
    }
}
