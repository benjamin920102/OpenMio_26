package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mioclient.module.render.Ambience;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Level.class})

public class MixinWorld {
    private static Ambience ambience;

    private static Ambience getAmbience() {
        if (ambience == null && ClientServices.moduleInjector != null) {
            try { ambience = (Ambience) ClientServices.moduleInjector.getModule(Ambience.class); } catch (Exception e) {}
        }
        return ambience;
    }

    @Inject(method = {"getRainLevel"}, at = {@At("HEAD")}, cancellable = true)
    private void getRainGradientHook(float f, CallbackInfoReturnable<Float> callbackInfoReturnable) {
        Ambience amb = getAmbience();
        if (amb != null && amb.worldWeather.getValue().booleanValue() && amb.isToggled()) {
            callbackInfoReturnable.setReturnValue(Float.valueOf(amb.weather.getValue() == Ambience.AmbiencePredicateMode.CLEAR ? 0.0f : 1.0f));
            callbackInfoReturnable.cancel();
        }
    }

    @ModifyExpressionValue(method = {"<init>"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;create()Lnet/minecraft/util/RandomSource;")}, require = 0)
    private RandomSource initHook(RandomSource random) {
        return RandomSource.createThreadSafe();
    }
}
