package me.mioclient.render.shader;

import me.mioclient.api.ClientAccessor;

import me.mioclient.ducks.DuckWorldRendererFramebuffer;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ShaderFramebufferSwapper.class */
public final class ShaderFramebufferSwapper implements ClientAccessor {
    public static net.minecraft.client.gl.Framebuffer framebuffer;

    public ShaderFramebufferSwapper() {
        throw new AssertionError();
    }

    public static void swapAndRender(PostProcessEffect framebufferHelper, boolean z, java.lang.Runnable runnable) {
        swapFramebuffer(framebufferHelper, z);
        runnable.run();
        minecraftClient.getBufferBuilders().getEntityVertexConsumers().draw();
        restoreFramebuffer(z);
    }

    public static void swapFramebuffer(PostProcessEffect framebufferHelper, boolean z) {
        if (z) {
            ShaderPassRegistry.beginOutlinePass();
        }
        framebuffer = ((DuckWorldRendererFramebuffer) minecraftClient.worldRenderer).getFramebuffer();
        ((DuckWorldRendererFramebuffer) minecraftClient.worldRenderer).setFramebuffer(framebufferHelper.framebuffer);
        minecraftClient.getFramebuffer().endWrite();
        ShaderPassRegistry.flag = true;
    }

    public static void restoreFramebuffer(boolean z) {
        ShaderPassRegistry.flag = false;
        ((DuckWorldRendererFramebuffer) minecraftClient.worldRenderer).setFramebuffer(framebuffer);
        minecraftClient.getFramebuffer().beginWrite(false);
        if (z) {
            ShaderPassRegistry.do866();
        }
    }
}
