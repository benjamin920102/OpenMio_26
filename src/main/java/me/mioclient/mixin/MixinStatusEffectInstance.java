package me.mioclient.mixin;

import me.mioclient.ducks.DuckStatusEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({MobEffectInstance.class})

public class MixinStatusEffectInstance implements DuckStatusEffectInstance {

    @Shadow
    private int duration;

    @Unique
    private int mio$initialDuration;

    @Inject(method = {"<init>(Lnet/minecraft/core/Holder;IIZZZLnet/minecraft/world/effect/MobEffectInstance;)V"}, at = {@At("TAIL")})
    private void initHook(Holder<MobEffect> registryEntry, int i, int i2, boolean z, boolean z2, boolean z3, MobEffectInstance statusEffectInstance, CallbackInfo callbackInfo) {
        this.mio$initialDuration = i;
    }

    @Override // me.mioclient.ducks.DuckStatusEffectInstance
    public float mio$getDurationRation() {
        return this.duration / this.mio$initialDuration;
    }
}
