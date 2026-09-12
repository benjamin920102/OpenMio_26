package me.mioclient.mixin.sodium;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.mioclient.module.render.Xray;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Pseudo
@Mixin(targets = {"me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer"}, remap = false)
/* loaded from: mio-yarn.jar:me/mioclient/mixin/sodium/MixinSodiumBlockRenderer.class */
public class MixinSodiumBlockRenderer {
    private static Xray xray;

    private static Xray getXray() {

        if (xray == null && ClientServices.moduleInjector != null) {

            try { xray = (Xray) ClientServices.moduleInjector.getModule(Xray.class); } catch (Exception e) {}

        }

        return xray;

    }

    @Inject(method = {"renderModel"}, at = {@At("HEAD")}, cancellable = true)
    private void onRenderModel(BlockRenderContext blockRenderContext, ChunkBuildBuffers chunkBuildBuffers, CallbackInfo callbackInfo) {
        Xray xray_lcl = getXray();

        if (xray_lcl == null) return;

        if (MinecraftClient.getInstance().player == null || !xray_lcl.isToggled() || xray_lcl.shouldRenderBlock(blockRenderContext.pos(), blockRenderContext.state().getBlock())) {
            return;
        }
        callbackInfo.cancel();
    }
}
