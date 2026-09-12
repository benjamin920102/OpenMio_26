package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import com.mojang.blaze3d.systems.RenderSystem;
import me.mioclient.module.render.NoRender;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({RenderLayers.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinRenderLayers.class */
public class MixinRenderLayers {
    private static NoRender norender;

    private static NoRender getNorender() {

        if (norender == null && ClientServices.moduleInjector != null) {

            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}

        }

        return norender;

    }

    @Inject(method = {"getEntityBlockLayer"}, at = {@At("HEAD")}, cancellable = true)
    private static void getEntityBlockLayer(BlockState blockState, boolean z, CallbackInfoReturnable<RenderLayer> callbackInfoReturnable) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.getHandOpacity() == 1.0f || RenderSystem.getShaderColor()[3] == 1.0f) {
            return;
        }
        callbackInfoReturnable.setReturnValue(TexturedRenderLayers.getEntityTranslucentCull());
        callbackInfoReturnable.cancel();
    }
}
