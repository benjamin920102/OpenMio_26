package me.mioclient.render.shader;

import me.mioclient.api.ClientAccessor;
import me.mioclient.module.render.esp.EspOutlineShaderPass;
import me.mioclient.module.render.shader.BloomShaderPass;
import me.mioclient.module.render.shader.GradientShaderPass;
import me.mioclient.module.render.shader.RainbowShaderPass;
import me.mioclient.module.render.shader.SolidShaderPass;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.render.VertexConsumerProvider;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ShaderPassRegistry.class */
public class ShaderPassRegistry implements ClientAccessor {
    public static OutlineShaderPass shaderFramebufferHelper;
    public static OutlineShaderPass shaderFramebufferHelper2;
    public static OutlineShaderPass shaderFramebufferHelper3;
    public static OutlineShaderPass shaderFramebufferHelper4;
    public static OutlineShaderPass shaderFramebufferHelper5;
    public static List<OutlineShaderPass> list;
    public static boolean flag;
    public static boolean flag2;

    public static void init() {
        shaderFramebufferHelper = new SolidShaderPass();
        shaderFramebufferHelper2 = new RainbowShaderPass();
        shaderFramebufferHelper3 = new GradientShaderPass();
        shaderFramebufferHelper4 = new BloomShaderPass();
        shaderFramebufferHelper5 = new EspOutlineShaderPass();
        list = List.of(shaderFramebufferHelper, shaderFramebufferHelper2, shaderFramebufferHelper3, shaderFramebufferHelper4, shaderFramebufferHelper5);
    }

    public static void beginOutlinePass() {
        Iterator<OutlineShaderPass> it = list.iterator();
        while (it.hasNext()) {
            it.next().beginOutlinePass();
        }
    }

    public static void do866() {
        Iterator<OutlineShaderPass> it = list.iterator();
        while (it.hasNext()) {
            it.next().do866();
        }
    }

    public static void resizeFramebuffers(int i, int i2) {
        if (minecraftClient == null) {
            return;
        }
        Iterator<OutlineShaderPass> it = list.iterator();
        while (it.hasNext()) {
            it.next().resizeFramebuffers(i, i2);
        }
    }

    public static boolean isProviderInUse(VertexConsumerProvider vertexConsumerProvider) {
        Iterator<OutlineShaderPass> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().outlineVertexConsumerProvider == vertexConsumerProvider) {
                return true;
            }
        }
        return false;
    }
}
