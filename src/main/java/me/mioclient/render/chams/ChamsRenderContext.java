package me.mioclient.render.chams;

import me.mioclient.mode.render.MatrixStackDataMode;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.client.util.math.MatrixStack;
import me.mioclient.mode.render.MatrixStackDataMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ChamsRenderContext.class */
public final class ChamsRenderContext {
    public final MatrixStack matrixStack;
    public final MatrixStackDataMode matrixStackDataMode;

    public ChamsRenderContext(MatrixStack matrixStack, MatrixStackDataMode matrixStackDataMode) {
        this.matrixStack = matrixStack;
        this.matrixStackDataMode = matrixStackDataMode;
    }




    public MatrixStack getMatrixStack() {
        return this.matrixStack;
    }

    public MatrixStackDataMode getRenderPass() {
        return this.matrixStackDataMode;
    }
}
