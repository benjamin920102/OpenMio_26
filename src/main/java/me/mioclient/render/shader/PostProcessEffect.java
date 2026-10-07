package me.mioclient.render.shader;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.render.mesh.GlStateWrapper;

import me.mioclient.api.ClientAccessor;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.lwjgl.glfw.GLFW;

public abstract class PostProcessEffect implements ClientAccessor {
    public Object outlineVertexConsumerProvider;
    public Framebuffer framebuffer;
    public ShaderProgram framebufferHelper_3;

    public void initShaderProgram(String str) {
        this.framebuffer = new Framebuffer(2);
        this.framebufferHelper_3 = new ShaderProgram("/base.vert", new StringTemplate().arg(str).format("/1.frag"));
    }

    public void initShaderProgramDual(String str, String str2) {
        this.framebuffer = new Framebuffer(2);
        this.framebufferHelper_3 = new ShaderProgram(new StringTemplate().arg(str).format("/1.vert"), new StringTemplate().arg(str2).format("/1.frag"));
    }

    public abstract boolean isOutlineShaderActive();

    public abstract boolean shouldOutlineEntity(Entity entity);

    public void pushFramebuffer() {
    }

    public void popFramebuffer() {
    }

    public abstract void applyOutlineUniforms();

    public void beginOutline() {
        if (isOutlineShaderActive() && this.framebuffer != null) {
            this.framebuffer.clear(false);
        }
    }

    public void applyPostEffect(java.lang.Runnable runnable) {
        if (isOutlineShaderActive()) {
            pushFramebuffer();
            runnable.run();
            popFramebuffer();
            if (this.framebufferHelper_3 == null) {
                return;
            }
            GlStateWrapper.bindTextureUnit(this.framebuffer.getColorAttachment(), 0);
            this.framebufferHelper_3.use();
            try {
                this.framebufferHelper_3.setUniformVec2f("u_Size", minecraftClient.getWindow().getWidth(), minecraftClient.getWindow().getHeight());
            } catch (Exception ignored) {
            }
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
        this.framebuffer.resize(i, i2, false);
    }
}
