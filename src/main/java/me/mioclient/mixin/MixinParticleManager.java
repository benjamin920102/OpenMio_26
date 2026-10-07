package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.events.render.AddParticleEvent;
import me.mioclient.module.render.NoRender;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* 26.2 port (verified via JDK25 javap): ParticleEngine.addParticle(Particle)
   -> add(Particle); addParticle(ParticleOptions,...) -> createParticle(...);
   addBlockBreakingParticles/addBlockBreakParticles gone — hooks deleted. */
@Mixin({ParticleEngine.class})

public abstract class MixinParticleManager {
    private static NoRender norender;

    private static NoRender getNorender() {

        if (norender == null && ClientServices.moduleInjector != null) {

            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}

        }

        return norender;

    }

    @Inject(method = {"add"}, at = {@At("HEAD")}, cancellable = true, require = 0)
    private void addParticle(Particle particle, CallbackInfo callbackInfo) {
        AddParticleEvent addParticleEvent = new AddParticleEvent(particle);
        ClientAccessor.eventBus.fireEvent(addParticleEvent);
        if (addParticleEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"createParticle"}, at = {@At("HEAD")}, cancellable = true, require = 0)
    private void addParticleHook(ParticleOptions particleEffect, double d, double d2, double d3, double d4, double d5, double d6, CallbackInfoReturnable<Particle> callbackInfoReturnable) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.isParticleBlocked(particleEffect)) {
            callbackInfoReturnable.setReturnValue(null);
            callbackInfoReturnable.cancel();
        }
    }
}
