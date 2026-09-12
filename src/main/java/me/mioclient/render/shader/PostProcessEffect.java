package me.mioclient.render.shader;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.render.mesh.GlStateWrapper;

import me.mioclient.api.ClientAccessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.entity.Entity;
import org.lwjgl.glfw.GLFW;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PostProcessEffect.class */
public abstract class PostProcessEffect implements ClientAccessor {
    public OutlineVertexConsumerProvider outlineVertexConsumerProvider;
    public net.minecraft.client.gl.Framebuffer framebuffer;
    public ShaderProgram framebufferHelper_3;

    public void initShaderProgram(String str) {
        this.outlineVertexConsumerProvider = new OutlineVertexConsumerProvider(minecraftClient.getBufferBuilders().getEntityVertexConsumers());
        this.framebuffer = new SimpleFramebuffer(minecraftClient.getWindow().getFramebufferWidth(), minecraftClient.getWindow().getFramebufferHeight(), false, MinecraftClient.IS_SYSTEM_MAC);
        this.framebuffer.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.framebufferHelper_3 = new ShaderProgram("/base.vert", new StringTemplate().arg(str).format("/\u0001.frag"));
    }

    public void initShaderProgramDual(String str, String str2) {
        this.outlineVertexConsumerProvider = new OutlineVertexConsumerProvider(minecraftClient.getBufferBuilders().getEntityVertexConsumers());
        this.framebuffer = new SimpleFramebuffer(minecraftClient.getWindow().getFramebufferWidth(), minecraftClient.getWindow().getFramebufferHeight(), false, MinecraftClient.IS_SYSTEM_MAC);
        this.framebuffer.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.framebufferHelper_3 = new ShaderProgram(new StringTemplate().arg(str).format("/\u0001.vert"), new StringTemplate().arg(str2).format("/\u0001.frag"));
    }

    public abstract boolean isOutlinePassActive();

    public abstract boolean shouldOutlineEntity(Entity entity);

    public void pushFramebuffer() {
    }

    public void popFramebuffer() {
    }

    public abstract void applyOutlineUniforms();

    public void beginOutlinePass() {
        if (isOutlinePassActive()) {
            this.framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
            minecraftClient.getFramebuffer().beginWrite(false);
        }
    }

    public void applyPostEffect(java.lang.Runnable runnable) {
        if (isOutlinePassActive()) {
            pushFramebuffer();
            runnable.run();
            popFramebuffer();
            minecraftClient.getFramebuffer().beginWrite(false);
            GlStateWrapper.bindTextureUnit(this.framebuffer.getColorAttachment(), 0);
            this.framebufferHelper_3.use();
            this.framebufferHelper_3.setUniformVec2f("u_Size", minecraftClient.getWindow().getFramebufferWidth(), minecraftClient.getWindow().getFramebufferHeight());
            this.framebufferHelper_3.setUniformInt("u_Texture", 0);
            this.framebufferHelper_3.setUniformFloat("u_Time", GLFW.glfwGetTime());
            applyOutlineUniforms();
            FullscreenQuad.drawBuffer();
        }
    }

    public void resizeFramebuffers(int i, int i2) {
        if (this.framebuffer == null) {
            return;
        }
        this.framebuffer.resize(i, i2, MinecraftClient.IS_SYSTEM_MAC);
    }
}
