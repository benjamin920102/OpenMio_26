package me.mioclient.render.shader;

import me.mioclient.api.ClientAccessor;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class PostEffectPipeline {
    public final Identifier id;
    public final Consumer<PostEffectPipeline> setup;
    public int num;
    public int num2;

    public PostEffectPipeline(Identifier identifier, Consumer<PostEffectPipeline> consumer) {
        this.id = identifier;
        this.setup = consumer;
        checkDimensions();
        try {
            consumer.accept(this);
        } catch (Exception ignored) {
        }
    }

    public static PostEffectPipeline createBlurPipeline(String str, Consumer<PostEffectPipeline> consumer) {
        return new PostEffectPipeline(Identifier.fromNamespaceAndPath(ClientAccessor.isGlobalsLoaded() ? "mio" : "minecraft", String.format("shaders/post/%s.json", str)), consumer);
    }

    public void checkDimensions() {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient == null || minecraftClient.getWindow() == null) {
            return;
        }
        int framebufferWidth = minecraftClient.getWindow().getWidth();
        int framebufferHeight = minecraftClient.getWindow().getHeight();
        if (this.num == framebufferWidth && this.num2 == framebufferHeight) {
            return;
        }
        this.num = framebufferWidth;
        this.num2 = framebufferHeight;
    }

    public void setUniform(String str, float f) {
    }

    public void bindSampler(String str, Object framebuffer) {
    }

    public void renderEffect(float f) {
        checkDimensions();
    }

    public Object getProcessor() {
        return null;
    }
}
