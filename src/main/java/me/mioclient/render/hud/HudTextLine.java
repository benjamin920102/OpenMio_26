package me.mioclient.render.hud;

import me.mioclient.api.ClientServices;

import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.anim.InterpolatedFloat;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.anim.InterpolatedFloat;

import java.awt.Color;
import java.util.function.Supplier;
import me.mioclient.module.client.HUD;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/HudTextLine.class */
public class HudTextLine {
    public static HUD hud = (HUD) ClientServices.moduleInjector.getModule(HUD.class);
    public final Supplier<Text> supplier;
    public final Supplier<Boolean> supplier2;
    public float val;
    public final InterpolatedFloat hUDHelper_2 = new InterpolatedFloat(Float.intBitsToFloat(1073741824), true);

    public HudTextLine(Supplier<Text> supplier, Supplier<Boolean> supplier2) {
        this.supplier = supplier;
        this.supplier2 = supplier2;
    }

    public void renderLine(DrawContext drawContext, float f, float f2, Color color) {
        float f3 = ClientTextRenderer.INSTANCE.getStringWidth(getAsText().getString());
        if (hud.setting3.getValue().booleanValue()) {
            this.hUDHelper_2.animate(f3);
        } else {
            this.hUDHelper_2.setImmediate(f3);
        }
        if (this.val > 0.0f) {
            ClientTextRenderer.INSTANCE.drawTextShadowObj(drawContext, getAsText(), f, f2, ColorUtils.withAlpha(color, (int) (color.getAlpha() * MathHelper.clamp(this.val * Float.intBitsToFloat(1073741824), Double.longBitsToDouble(4591870180066957722L), Double.longBitsToDouble(4607182418800017408L)))));
        }
    }

    public void recalculateLayout() {
        float intBitsToFloat = Float.intBitsToFloat(1065353216) / (ClientServices.hitmarkerHelper.getFrameRate() >> 2);
        if (isDrawn()) {
            this.val += intBitsToFloat;
        } else {
            this.val -= intBitsToFloat;
        }
        this.val = MathHelper.clamp(this.val, 0.0f, Float.intBitsToFloat(1065353216));
    }

    public float getAlpha() {
        return this.val;
    }

    public boolean isDrawn() {
        return this.supplier2.get().booleanValue();
    }

    public Text getAsText() {
        return this.supplier.get();
    }
}
