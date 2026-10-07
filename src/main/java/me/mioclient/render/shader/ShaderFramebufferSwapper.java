package me.mioclient.render.shader;

import me.mioclient.api.ClientAccessor;

import me.mioclient.ducks.DuckWorldRendererFramebuffer;

public final class ShaderFramebufferSwapper implements ClientAccessor {
    public static Object framebuffer;

    public ShaderFramebufferSwapper() {
        throw new AssertionError();
    }

    public static void swapAndRender(PostProcessEffect framebufferHelper, boolean z, java.lang.Runnable runnable) {
        swapFramebuffer(framebufferHelper, z);
        runnable.run();
        restoreFramebuffer(z);
    }

    public static void swapFramebuffer(PostProcessEffect framebufferHelper, boolean z) {
        if (z) {
            ShaderRegistry.beginOutline();
        }
        try {
            framebuffer = ((DuckWorldRendererFramebuffer) minecraftClient.levelRenderer).getFramebuffer();
            invokeSetFramebuffer(minecraftClient.levelRenderer, framebufferHelper.framebuffer);
        } catch (Exception ignored) {
        }
        ShaderRegistry.flag = true;
    }

    public static void restoreFramebuffer(boolean z) {
        ShaderRegistry.flag = false;
        invokeSetFramebuffer(minecraftClient.levelRenderer, framebuffer);
        if (z) {
            ShaderRegistry.do866();
        }
    }

    // 26.2: net.minecraft.client.gl.Framebuffer is gone, so the duck setter's
    // param type can't be named here; dispatch by name until the duck is re-ported.
    private static void invokeSetFramebuffer(Object target, Object value) {
        for (java.lang.reflect.Method method : target.getClass().getMethods()) {
            if (method.getName().equals("setFramebuffer") && method.getParameterCount() == 1) {
                try {
                    method.invoke(target, value);
                } catch (Exception ignored) {
                }
                return;
            }
        }
    }
}
