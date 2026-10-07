package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.module.render.Ambience;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Biome.class})

public class MixinBiome {
    private static Ambience ambience;

    private static Ambience getAmbience() {

        if (ambience == null && ClientServices.moduleInjector != null) {

            try { ambience = (Ambience) ClientServices.moduleInjector.getModule(Ambience.class); } catch (Exception e) {}

        }

        return ambience;

    }

    @Inject(method = {"getPrecipitationAt"}, at = {@At("HEAD")}, cancellable = true)
    private void getPrecipitationHook(BlockPos blockPos, int i, CallbackInfoReturnable<Biome.Precipitation> callbackInfoReturnable) {
        Ambience ambience_lcl = getAmbience();

        if (ambience_lcl == null) return;

        if (ambience_lcl.isToggled() && ambience_lcl.worldWeather.getValue().booleanValue()) {
            callbackInfoReturnable.cancel();
            callbackInfoReturnable.setReturnValue(ambience_lcl.weather.getValue().getPrecipitation2196());
        }
    }
}
