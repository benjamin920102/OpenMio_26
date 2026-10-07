package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;

import net.minecraft.world.level.block.Block;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import me.mioclient.event.events.entity.RemoveEntityEvent;
import me.mioclient.module.render.Ambience;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* 26.2 port (verified): DimensionSpecialEffects AND DimensionEffects are gone
   (sky rendering reworked) — custom dimension effects, getSkyColor and
   getDimensionEffects hooks removed with them. Ambience dust now hooks
   doAnimateTick and spawns AmbientParticle ash directly; biome AmbientParticleSettings
   is now the AmbientParticle record. removeEntity(int, RemovalReason) survives. */
@Mixin({ClientLevel.class})
public abstract class MixinClientWorld {
    private static Ambience ambience;

    private static Ambience getAmbience() {

        if (ambience == null && ClientServices.moduleInjector != null) {

            try { ambience = (Ambience) ClientServices.moduleInjector.getModule(Ambience.class); } catch (Exception e) {}

        }

        return ambience;

    }

    @Shadow
    public abstract void addParticle(ParticleOptions particleEffect, double d, double d2, double d3, double d4, double d5, double d6);

    @Inject(method = {"doAnimateTick"}, at = {@At("TAIL")})
    private void dustParticleTickHook(int xt, int yt, int zt, int r, RandomSource animateRandom, Block markerParticleTarget, BlockPos.MutableBlockPos pos, CallbackInfo callbackInfo) {
        Ambience ambience_lcl = getAmbience();

        if (ambience_lcl == null) return;

        if (ambience_lcl.isToggled() && ambience_lcl.weather.getValue() == Ambience.AmbiencePredicateMode.DUSTY && ambience_lcl.worldWeather.getValue().booleanValue()) {
            AmbientParticle setting = new AmbientParticle(animateRandom.nextBoolean() ? ParticleTypes.WHITE_ASH : ParticleTypes.ASH, 0.2f * ambience_lcl.amount.getValue().floatValue());
            if (setting.canSpawn(animateRandom)) {
                addParticle(setting.particle(), pos.getX() + animateRandom.nextDouble(), pos.getY() + animateRandom.nextDouble(), pos.getZ() + animateRandom.nextDouble(), 0.0d, 0.0d, 0.0d);
            }
        }
    }

    @Inject(method = {"removeEntity"}, at = {@At("HEAD")}, cancellable = true)
    private void removeEntityHook(int i, Entity.RemovalReason removalReason, CallbackInfo callbackInfo) {
        RemoveEntityEvent removeEntityEvent = new RemoveEntityEvent(i);
        ClientAccessor.eventBus.fireEvent(removeEntityEvent);
        if (removeEntityEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}
