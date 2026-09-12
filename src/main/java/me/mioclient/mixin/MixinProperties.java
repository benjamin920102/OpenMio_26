package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;

import me.mioclient.module.render.SkyColor;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({ClientWorld.Properties.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinProperties.class */
public class MixinProperties {
    private static SkyColor skycolor;

    private static SkyColor getSkycolor() {

        if (skycolor == null && ClientServices.moduleInjector != null) {

            try { skycolor = (SkyColor) ClientServices.moduleInjector.getModule(SkyColor.class); } catch (Exception e) {}

        }

        return skycolor;

    }

    @Inject(method = {"getHorizonShadingRatio"}, at = {@At("HEAD")}, cancellable = true)
    private void onGetSkyProperties(CallbackInfoReturnable<Float> callbackInfoReturnable) {
        SkyColor skycolor_lcl = getSkycolor();

        if (skycolor_lcl == null) return;

        if (ClientAccessor.minecraftClient.world != null && skycolor_lcl.isToggled() && skycolor_lcl.isDimensionEnabled() && !ClientAccessor.minecraftClient.world.getRegistryKey().getValue().getPath().contains("over")) {
            callbackInfoReturnable.setReturnValue(Float.valueOf(1.0f));
            callbackInfoReturnable.cancel();
        }
    }
}
