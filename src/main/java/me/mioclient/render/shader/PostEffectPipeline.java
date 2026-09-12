package me.mioclient.render.shader;

import me.mioclient.api.ClientAccessor;

import me.mioclient.ducks.DuckPostEffectProcessor;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.JsonEffectShaderProgram;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.util.Identifier;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PostEffectPipeline.class */
public class PostEffectPipeline {
    public final PostEffectProcessor postEffectProcessor;
    public int num;
    public int num2;

    public PostEffectPipeline(Identifier identifier, Consumer<PostEffectPipeline> consumer) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        try {
            this.postEffectProcessor = new PostEffectProcessor(minecraftClient.getTextureManager(), minecraftClient.getResourceManager(), minecraftClient.getFramebuffer(), identifier);
            checkDimensions();
            consumer.accept(this);
        } catch (Exception e) {
            throw new java.lang.RuntimeException(e);
        }
    }

    public static PostEffectPipeline createBlurPipeline(String str, Consumer<PostEffectPipeline> consumer) {
        return new PostEffectPipeline(Identifier.of(ClientAccessor.isGlobalsLoaded() ? "mio" : "minecraft", String.format("shaders/post/%s.json", str)), consumer);
    }

    public void checkDimensions() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        int framebufferWidth = minecraftClient.getWindow().getFramebufferWidth();
        int framebufferHeight = minecraftClient.getWindow().getFramebufferHeight();
        if (this.num == framebufferWidth && this.num2 == framebufferHeight) {
            return;
        }
        this.postEffectProcessor.setupDimensions(framebufferWidth, framebufferHeight);
        this.num = framebufferWidth;
        this.num2 = framebufferHeight;
    }

    public void setUniform(String str, float f) {
        ((DuckPostEffectProcessor) this.postEffectProcessor).getPasses().stream().map(postEffectPass -> {
            return postEffectPass.getProgram().getUniformByName(str);
        }).filter((v0) -> {
            return Objects.nonNull(v0);
        }).forEach(glUniform -> {
            glUniform.set(f);
        });
    }

    public void bindSampler(String str, net.minecraft.client.gl.Framebuffer framebuffer) {
        Iterator<PostEffectPass> it = ((DuckPostEffectProcessor) this.postEffectProcessor).getPasses().iterator();
        while (it.hasNext()) {
            JsonEffectShaderProgram program = it.next().getProgram();
            Objects.requireNonNull(framebuffer);
            program.bindSampler(str, framebuffer::getColorAttachment);
        }
    }

    public void renderEffect(float f) {
        checkDimensions();
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.resetTextureMatrix();
        this.postEffectProcessor.render(f);
        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);
        RenderSystem.disableBlend();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.enableDepthTest();
    }

    public PostEffectProcessor getProcessor() {
        return this.postEffectProcessor;
    }
}
