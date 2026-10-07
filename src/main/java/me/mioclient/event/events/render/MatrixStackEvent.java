package me.mioclient.event.events.render;

import me.mioclient.event.Event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;

public class MatrixStackEvent extends Event {
    public PoseStack matrixStack;
    public float val;
    public GuiGraphicsExtractor drawContext;
    /* Set by the world-render driver (MixinLevelRenderer); null outside world rendering. */
    public CameraRenderState cameraState;

    
    public static class Inner extends MatrixStackEvent {
        public static final Inner inner = new Inner();

        public static Inner getInner933(PoseStack matrixStack, float f) {
            inner.matrixStack = matrixStack;
            inner.val = f;
            inner.resetCancelled();
            return inner;
        }
    }

    
    public static class Inner_2 extends MatrixStackEvent {
        public static final Inner_2 inner_2 = new Inner_2();

        public static Inner_2 getInner_23016(PoseStack matrixStack, float f) {
            inner_2.matrixStack = matrixStack;
            inner_2.val = f;
            inner_2.drawContext = Inner_3.inner_3.getDrawContext();
            inner_2.resetCancelled();
            return inner_2;
        }
    }

    
    public static class Inner_3 extends MatrixStackEvent {
        public static final Inner_3 inner_3 = new Inner_3();

        public static Inner_3 getInner_3133(PoseStack matrixStack, float f) {
            inner_3.matrixStack = matrixStack;
            inner_3.val = f;
            inner_3.drawContext = createDrawContext(matrixStack);
            inner_3.resetCancelled();
            return inner_3;
        }

        /* 26.2 backend: world drawing goes through GizmoRenderer (vanilla gizmos),
           so there is no draw context on the world event. HUD/screen events carry
           their own live contexts. */
        private static GuiGraphicsExtractor createDrawContext(PoseStack matrixStack) {
            return null;
        }
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
