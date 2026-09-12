package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import me.mioclient.module.render.Ambience;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({Biome.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinBiome.class */
public class MixinBiome {
    private static Ambience ambience;

    private static Ambience getAmbience() {

        if (ambience == null && ClientServices.moduleInjector != null) {

            try { ambience = (Ambience) ClientServices.moduleInjector.getModule(Ambience.class); } catch (Exception e) {}

        }

        return ambience;

    }

    @Inject(method = {"getPrecipitation"}, at = {@At("HEAD")}, cancellable = true)
    private void getPrecipitationHook(BlockPos blockPos, CallbackInfoReturnable<Biome.Precipitation> callbackInfoReturnable) {
        Ambience ambience_lcl = getAmbience();

        if (ambience_lcl == null) return;

        if (ambience_lcl.isToggled() && ambience_lcl.worldWeather.getValue().booleanValue()) {
            callbackInfoReturnable.cancel();
            callbackInfoReturnable.setReturnValue(ambience_lcl.weather.getValue().getPrecipitation2196());
        }
    }
}
