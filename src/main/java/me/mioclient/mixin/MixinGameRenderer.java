package me.mioclient.mixin;

import me.mioclient.api.ClientServices;
import me.mioclient.mixin.ducks.DuckLevelRenderer;
import me.mioclient.module.render.Shader;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* 26.2 outline chains (same shape as Disa's): right before vanilla composites
   entity outlines, run the active custom chain over the outline target. Vanilla
   still runs afterwards as fallback, so a missing/broken chain degrades to plain
   outlines instead of nothing. Static chain uniforms ship inside the JSONs;
   per-frame UBO animation is a follow-up, not required for the looks. */
@Mixin({GameRenderer.class})
public abstract class MixinGameRenderer {
    @Inject(method = {"render(Lnet/minecraft/client/DeltaTracker;Z)V"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V")})
    private void mio$runOutlineChain(DeltaTracker tracker, boolean tick, CallbackInfo ci) {
        try {
            if (ClientServices.moduleInjector == null) {
                return;
            }
            Shader shader;
            try {
                shader = (Shader) ClientServices.moduleInjector.getModule(Shader.class);
            } catch (Exception e) {
                return;
            }
            if (shader == null || !shader.isToggled()) {
                return;
            }
            Identifier variant = shader.pickActiveOutlineChain();
            if (variant == null) {
                return;
            }
            Minecraft mc = Minecraft.getInstance();
            if (mc.levelRenderer == null || mc.getShaderManager() == null) {
                return;
            }
            RenderTarget outline = null;
            try {
                outline = mc.levelRenderer.entityOutlineTarget();
            } catch (Exception ignored) {
            }
            if (outline == null) {
                try {
                    outline = ((DuckLevelRenderer) mc.levelRenderer).mio$getEntityOutlineTargetField();
                } catch (Exception e) {
                    return;
                }
                if (outline == null) {
                    return;
                }
            }
            PostChain chain;
            try {
                chain = mc.getShaderManager().getPostChain(variant, LevelTargetBundle.MAIN_TARGETS);
            } catch (Exception e) {
                return;
            }
            if (chain == null) {
                return;
            }
            chain.process(outline, GraphicsResourceAllocator.UNPOOLED);
        } catch (Exception ignored) {
        }
    }
}
