package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;

import me.mioclient.event.events.render.AddParticleEvent;
import me.mioclient.module.render.NoRender;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({ParticleManager.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinParticleManager.class */
public abstract class MixinParticleManager {
    private static NoRender norender;

    private static NoRender getNorender() {

        if (norender == null && ClientServices.moduleInjector != null) {

            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}

        }

        return norender;

    }

    @Shadow
    @Nullable
    protected abstract <T extends ParticleEffect> Particle method_3055(T t, double d, double d2, double d3, double d4, double d5, double d6);

    @Inject(method = {"addParticle(Lnet/minecraft/client/particle/Particle;)V"}, at = {@At("HEAD")}, cancellable = true)
    private void addParticle(Particle particle, CallbackInfo callbackInfo) {
        AddParticleEvent addParticleEvent = new AddParticleEvent(particle);
        ClientAccessor.eventBus.fireEvent(addParticleEvent);
        if (addParticleEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;"}, at = {@At("HEAD")}, cancellable = true)
    private void addParticleHook(ParticleEffect particleEffect, double d, double d2, double d3, double d4, double d5, double d6, CallbackInfoReturnable<Particle> callbackInfoReturnable) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.isParticleBlocked(particleEffect)) {
            callbackInfoReturnable.setReturnValue(method_3055(particleEffect, d, d2, d3, d4, d5, d6));
            callbackInfoReturnable.cancel();
        }
    }

    @Inject(method = {"addBlockBreakingParticles"}, at = {@At("HEAD")}, cancellable = true)
    private void addBlockBreakingParticlesHook(BlockPos blockPos, Direction direction, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.shouldBlockBlockBreaking()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"addBlockBreakParticles"}, at = {@At("HEAD")}, cancellable = true)
    private void addBlockBreakParticlesHook(BlockPos blockPos, BlockState blockState, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.shouldBlockBlockBreaking()) {
            callbackInfo.cancel();
        }
    }
}
