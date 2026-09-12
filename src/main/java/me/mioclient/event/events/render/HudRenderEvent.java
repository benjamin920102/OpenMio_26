package me.mioclient.event.events.render;

import me.mioclient.event.Event;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/MatrixStackEvent_2.class */
public class HudRenderEvent extends Event {
    public static final HudRenderEvent matrixStackEvent_2 = new HudRenderEvent();
    public MatrixStack matrixStack;
    public DrawContext drawContext;
    public float val;

    public static HudRenderEvent getMatrixStackEvent_21234(MatrixStack matrixStack, DrawContext drawContext, float f) {
        matrixStackEvent_2.matrixStack = matrixStack;
        matrixStackEvent_2.val = f;
        matrixStackEvent_2.drawContext = drawContext;
        matrixStackEvent_2.resetCancelled();
        return matrixStackEvent_2;
    }

    public MatrixStack getMatrixStack() {
        return this.matrixStack;
    }

    public float getTickDelta() {
        return this.val;
    }

    public DrawContext getDrawContext() {
        return this.drawContext;
    }
}
