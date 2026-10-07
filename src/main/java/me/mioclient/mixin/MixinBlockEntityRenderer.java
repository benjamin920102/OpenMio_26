package me.mioclient.mixin;

import me.mioclient.render.shader.ShaderRegistry;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* 26.2 port: BlockEntityRenderer.getRenderDistance renamed to getViewDistance. */
@Mixin({BlockEntityRenderer.class})

public interface MixinBlockEntityRenderer {
    @Inject(method = {"getViewDistance"}, at = {@At("HEAD")}, cancellable = true)
    private void getRenderDistanceHook(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        if (ShaderRegistry.flag) {
            callbackInfoReturnable.setReturnValue(1024);
        }
    }
}
