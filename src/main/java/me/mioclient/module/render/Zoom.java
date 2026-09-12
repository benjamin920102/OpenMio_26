package me.mioclient.module.render;

import me.mioclient.util.anim.ValueAnimator;

import me.mioclient.module.render.zoom.ZoomFovListener;
import me.mioclient.util.anim.ValueAnimator;

import me.mioclient.event.EventBus;

import me.mioclient.render.RenderConstants;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.input.MouseScrollEvent;
import me.mioclient.module.Module;
import net.minecraft.util.math.MathHelper;
import me.mioclient.ducks.DuckSimpleOption;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/Zoom.class */
public class Zoom extends Module {
    public Setting<Boolean> smooth = add(new BooleanSetting("Smooth", true));
    public Setting<Boolean> smoothCamera = add(new BooleanSetting("SmoothCamera", true));
    public Setting<Boolean> scroll = add(new BooleanSetting("Scroll", false));
    public Setting<Integer> amount = add(new NumberSetting<>("Amount", 2, 2, 5));
    public final ZoomFovListener zoomSearchHelper4;
    public int num;
    public boolean flag;
    public boolean flag2;
    public final ValueAnimator zoomHelper;
    public final ValueAnimator zoomHelper2;

    public Zoom() {
        super("Zoom", "Zooms your camera in.", Category.RENDER, new String[0]);
        this.zoomSearchHelper4 = new ZoomFovListener(this);
        this.zoomHelper = new ValueAnimator();
        this.zoomHelper2 = new ValueAnimator();
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        if (isNotInGame()) {
            return;
        }
        this.zoomHelper.animateTo(0.0f, getAnimationDuration(100));
        this.zoomHelper2.animateTo(0.0f, getAnimationDuration(100));
        minecraftClient.options.smoothCameraEnabled = this.flag;
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        if (isNotInGame()) {
            disable();
            return;
        }
        eventBus.register(this.zoomSearchHelper4);
        if (!this.flag2) {
            this.num = ((Integer) minecraftClient.options.getFov().getValue()).intValue();
        }
        this.zoomHelper.animateTo(Float.intBitsToFloat(1065353216), getAnimationDuration(RenderConstants.num));
        this.flag = minecraftClient.options.smoothCameraEnabled;
    }

    @Listen
    public void onMouseScroll(MouseScrollEvent mouseScrollEvent) {
        if (mouseScrollEvent.getVertical() == 0.0d || !this.scroll.getValue().booleanValue()) {
            return;
        }
        int abs = (int) (this.num - (Math.abs((75 / this.amount.getValue().intValue()) - this.num) * getSmoothProgress()));
        this.zoomHelper2.animateTo((int) MathHelper.clamp((float) (this.zoomHelper2.getValue() + (mouseScrollEvent.getVertical() * Double.longBitsToDouble(4621819117588971520L))), abs - this.num, abs - 10), getAnimationDuration(100));
        mouseScrollEvent.cancel();
    }

    public void forceSetFov(int i) {
        ((me.mioclient.ducks.DuckSimpleOption) (Object) minecraftClient.options.getFov()).forceSetValue(Integer.valueOf(i));
    }

    public int getZoomTargetFov() {
        int abs = (int) (this.num - (Math.abs((75 / this.amount.getValue().intValue()) - this.num) * getSmoothProgress()));
        return this.scroll.getValue().booleanValue() ? (int) MathHelper.clamp(abs - this.zoomHelper2.getValue(), Float.intBitsToFloat(1092616192), Float.intBitsToFloat(1125515264)) : abs;
    }

    public float getSmoothProgress() {
        return (float) (Double.longBitsToDouble(4607182418800017408L) - Math.pow(Float.intBitsToFloat(1065353216) - this.zoomHelper.getValue(), Double.longBitsToDouble(4616189618054758400L)));
    }

    public int getAnimationDuration(int i) {
        if (this.smooth.getValue().booleanValue()) {
            return i;
        }
        return 0;
    }
}
