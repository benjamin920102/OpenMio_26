package me.mioclient.render.chams;

import me.mioclient.mode.render.MatrixStackDataMode;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import com.mojang.blaze3d.vertex.PoseStack;

public final class ChamsRenderContext {
    public final PoseStack matrixStack;
    public final MatrixStackDataMode matrixStackDataMode;

    public ChamsRenderContext(PoseStack matrixStack, MatrixStackDataMode matrixStackDataMode) {
        this.matrixStack = matrixStack;
        this.matrixStackDataMode = matrixStackDataMode;
    }

    public PoseStack getMatrixStack() {
        return this.matrixStack;
    }

    public MatrixStackDataMode getRenderPass() {
        return this.matrixStackDataMode;
    }
}
