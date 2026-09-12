package me.mioclient.render.shader;

import me.mioclient.render.RenderUtil;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/BlurFramebuffer.class */
public class BlurFramebuffer extends net.minecraft.client.gl.Framebuffer {
    public static BlurFramebuffer blurFramebuffer;

    public BlurFramebuffer(int i, int i2) {
        super(false);
        RenderSystem.assertOnRenderThreadOrInit();
        resize(i, i2, true);
        setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public static BlurFramebuffer getInstance() {
        if (blurFramebuffer == null) {
            blurFramebuffer = new BlurFramebuffer(MinecraftClient.getInstance().getFramebuffer().textureWidth, MinecraftClient.getInstance().getFramebuffer().textureHeight);
        }
        return blurFramebuffer;
    }

    public static void runWithBlur(java.lang.Runnable runnable) {
        net.minecraft.client.gl.Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
        RenderSystem.assertOnRenderThreadOrInit();
        BlurFramebuffer blurFramebuffer1999 = getInstance();
        if (blurFramebuffer1999.textureWidth != framebuffer.textureWidth || blurFramebuffer1999.textureHeight != framebuffer.textureHeight) {
            blurFramebuffer1999.resize(framebuffer.textureWidth, framebuffer.textureHeight, false);
        }
        GlStateManager._glBindFramebuffer(36009, blurFramebuffer1999.fbo);
        blurFramebuffer1999.beginWrite(true);
        runnable.run();
        blurFramebuffer1999.endWrite();
        GlStateManager._glBindFramebuffer(36009, framebuffer.fbo);
        framebuffer.beginWrite(false);
    }

    public static void setBlurRadius(float f) {
        net.minecraft.client.gl.Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
        BlurFramebuffer blurFramebuffer1999 = getInstance();
        BlurMaskEffect.blurFramebufferHelper.bindSampler("MaskSampler", blurFramebuffer1999);
        BlurMaskEffect.blurFramebufferHelper.setUniform("Radius", f);
        BlurMaskEffect.blurFramebufferHelper.renderEffect(RenderUtil.getPartialTick());
        GlStateManager._glBindFramebuffer(36009, blurFramebuffer1999.fbo);
        blurFramebuffer1999.clear(true);
        GlStateManager._glBindFramebuffer(36009, framebuffer.fbo);
        framebuffer.beginWrite(true);
    }

    public static void renderWithBlur(java.lang.Runnable runnable, float f) {
        runWithBlur(runnable);
        setBlurRadius(f);
    }
}
