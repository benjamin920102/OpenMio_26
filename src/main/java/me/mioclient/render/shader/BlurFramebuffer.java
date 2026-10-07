package me.mioclient.render.shader;

import me.mioclient.render.RenderUtil;

import net.minecraft.client.Minecraft;

public class BlurFramebuffer {
    public static BlurFramebuffer blurFramebuffer;
    public int textureWidth = -1;
    public int textureHeight = -1;
    public int fbo = -1;

    public BlurFramebuffer(int i, int i2) {
        resize(i, i2, true);
    }

    public static BlurFramebuffer getInstance() {
        if (blurFramebuffer == null) {
            blurFramebuffer = new BlurFramebuffer(16, 16);
        }
        return blurFramebuffer;
    }

    public static void runWithBlur(java.lang.Runnable runnable) {
        runnable.run();
    }

    public static void setBlurRadius(float f) {
        BlurMaskEffect.blurFramebufferHelper.setUniform("Radius", f);
        BlurMaskEffect.blurFramebufferHelper.renderEffect(RenderUtil.getPartialTick());
    }

    public static void renderWithBlur(java.lang.Runnable runnable, float f) {
        runWithBlur(runnable);
        setBlurRadius(f);
    }

    public void resize(int i, int i2, boolean bl) {
        this.textureWidth = i;
        this.textureHeight = i2;
    }

    public void setClearColor(float r, float g, float b, float a) {
    }

    public void beginWrite(boolean bl) {
    }

    public void endWrite() {
    }

    public void clear(boolean bl) {
    }
}
