package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import me.mioclient.module.render.NoRender;
import net.minecraft.world.chunk.light.ChunkSkyLightProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({ChunkSkyLightProvider.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinChunkSkyLightProvider.class */
public class MixinChunkSkyLightProvider {
    private static NoRender norender;

    private static NoRender getNorender() {

        if (norender == null && ClientServices.moduleInjector != null) {

            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}

        }

        return norender;

    }

    @Inject(at = {@At("HEAD")}, method = {"method_51585"}, cancellable = true)
    private void recalculateLevelhook(int i, int i2, int i3, CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.isToggled() && norender_lcl.skyLight.getValue().booleanValue()) {
            callbackInfoReturnable.setReturnValue(15);
            callbackInfoReturnable.cancel();
        }
    }
}
