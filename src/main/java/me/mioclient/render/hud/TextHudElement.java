package me.mioclient.render.hud;

import me.mioclient.module.HudModule;

import me.mioclient.render.font.ClientTextRenderer;


import java.awt.Color;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import me.mioclient.module.HudModule;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/TextHudElement.class */
public class TextHudElement extends DraggableHudElement {
    public final List<? extends HudTextLine> list;

    public TextHudElement(HudModule moduleList, HudTextLine cryptoHelper) {
        this(moduleList, (List<? extends HudTextLine>) Collections.singletonList(cryptoHelper));
    }

    public TextHudElement(HudModule moduleList, List<? extends HudTextLine> list) {
        super(moduleList);
        this.list = list;
    }

    @Override // me.mioclient.render.hud.DraggableHudElement, me.mioclient.gui.GuiComponent
    public void render(DrawContext drawContext, MatrixStack matrixStack, double d, double d2) {
        super.render(drawContext, matrixStack, d, d2);
        float f = getAlignedY(getFontHeight());
        for (HudTextLine cryptoHelper : this.list) {
            matrixStack.push();
            float clamp = (float) MathHelper.clamp(Double.longBitsToDouble(4607182418800017408L) - Math.pow(Float.intBitsToFloat(1065353216) - cryptoHelper.getAlpha(), Double.longBitsToDouble(4616189618054758400L)), 0.0d, Double.longBitsToDouble(4607182418800017408L));
            Color color489 = getLineColor(f, cryptoHelper);
            float f2 = cryptoHelper.hUDHelper_2.getValue();
            matrixStack.translate(getAlignedX(f2) - ((f2 * (Float.intBitsToFloat(1065353216) - clamp)) * getHorizontalSide()), f, 0.0f);
            cryptoHelper.renderLine(drawContext, 0.0f, 0.0f, color489);
            if (clamp != 0.0f) {
                f += ((getFontHeight() * clamp) + Float.intBitsToFloat(1065353216)) * getVerticalSide();
            }
            matrixStack.pop();
        }
    }

    @Override // me.mioclient.render.hud.DraggableHudElement
    public float[] getSize() {
        float f = 0.0f;
        float f2 = 0.0f;
        for (HudTextLine cryptoHelper : this.list) {
            cryptoHelper.recalculateLayout();
            if (cryptoHelper.getAlpha() != 0.0f) {
                float clamp = (float) MathHelper.clamp(Double.longBitsToDouble(4607182418800017408L) - Math.pow(Float.intBitsToFloat(1065353216) - cryptoHelper.getAlpha(), Double.longBitsToDouble(4616189618054758400L)), 0.0d, Double.longBitsToDouble(4607182418800017408L));
                float f3 = ClientTextRenderer.INSTANCE.getStringWidth(cryptoHelper.getAsText().getString());
                f += (getFontHeight() * clamp) + Float.intBitsToFloat(1065353216);
                if (f3 > f2) {
                    f2 = f3;
                }
            }
        }
        if (!flag && f2 == 0.0f) {
            f2 = Float.intBitsToFloat(1112014848);
            f = getFontHeight();
        }
        return new float[]{f2, f};
    }

    public List<? extends HudTextLine> getLines() {
        return this.list;
    }

    public Color getLineColor(float f, HudTextLine cryptoHelper) {
        return this.moduleList.getHudColor(f);
    }
}
