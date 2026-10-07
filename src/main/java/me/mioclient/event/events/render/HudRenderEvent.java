package me.mioclient.event.events.render;

import me.mioclient.event.Event;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.mojang.blaze3d.vertex.PoseStack;

public class HudRenderEvent extends Event {
    public static final HudRenderEvent matrixStackEvent_2 = new HudRenderEvent();
    public PoseStack matrixStack;
    public GuiGraphicsExtractor drawContext;
    public float val;

    public static HudRenderEvent getMatrixStackEvent_21234(PoseStack matrixStack, GuiGraphicsExtractor drawContext, float f) {
        matrixStackEvent_2.matrixStack = matrixStack;
        matrixStackEvent_2.val = f;
        matrixStackEvent_2.drawContext = drawContext;
        matrixStackEvent_2.resetCancelled();
        return matrixStackEvent_2;
    }

    public PoseStack getMatrixStack() {
        return this.matrixStack;
    }

    public float getTickDelta() {
        return this.val;
    }

    public GuiGraphicsExtractor getDrawContext() {
        return this.drawContext;
    }
}
