package me.mioclient.render.shader;

import me.mioclient.api.ClientAccessor;
import me.mioclient.module.render.esp.EspOutlineShader;
import me.mioclient.module.render.shader.BloomShader;
import me.mioclient.module.render.shader.GradientShader;
import me.mioclient.module.render.shader.RainbowShader;
import me.mioclient.module.render.shader.SolidShader;

import java.util.Iterator;
import java.util.List;

public class ShaderRegistry implements ClientAccessor {
    public static OutlineShader shaderFramebufferHelper;
    public static OutlineShader shaderFramebufferHelper2;
    public static OutlineShader shaderFramebufferHelper3;
    public static OutlineShader shaderFramebufferHelper4;
    public static OutlineShader shaderFramebufferHelper5;
    public static List<OutlineShader> list;
    public static boolean flag;
    public static boolean flag2;

    /* Idempotent: safe to call from the render thread on first use. Passes that
       fail to construct are skipped so one bad shader never kills the registry. */
    public static void init() {
        if (list != null) {
            return;
        }
        try {
            shaderFramebufferHelper = new SolidShader();
        } catch (Throwable ignored) {
        }
        try {
            shaderFramebufferHelper2 = new RainbowShader();
        } catch (Throwable ignored) {
        }
        try {
            shaderFramebufferHelper3 = new GradientShader();
        } catch (Throwable ignored) {
        }
        try {
            shaderFramebufferHelper4 = new BloomShader();
        } catch (Throwable ignored) {
        }
        try {
            shaderFramebufferHelper5 = new EspOutlineShader();
        } catch (Throwable ignored) {
        }
        java.util.ArrayList<OutlineShader> built = new java.util.ArrayList<>();
        for (OutlineShader outline : new OutlineShader[]{shaderFramebufferHelper, shaderFramebufferHelper2, shaderFramebufferHelper3, shaderFramebufferHelper4, shaderFramebufferHelper5}) {
            if (outline != null) {
                built.add(outline);
            }
        }
        list = java.util.List.copyOf(built);
    }

    public static void beginOutline() {
        if (list == null) {
            return;
        }
        Iterator<OutlineShader> it = list.iterator();
        while (it.hasNext()) {
            it.next().beginOutline();
        }
    }

    public static void do866() {
        if (list == null) {
            return;
        }
        Iterator<OutlineShader> it = list.iterator();
        while (it.hasNext()) {
            it.next().do866();
        }
    }

    public static void resizeFramebuffers(int i, int i2) {
        if (minecraftClient == null || list == null) {
            return;
        }
        Iterator<OutlineShader> it = list.iterator();
        while (it.hasNext()) {
            it.next().resizeFramebuffers(i, i2);
        }
    }

    public static boolean isProviderInUse(Object vertexConsumerProvider) {
        if (list == null) {
            return false;
        }
        Iterator<OutlineShader> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().outlineVertexConsumerProvider == vertexConsumerProvider) {
                return true;
            }
        }
        return false;
    }
}
