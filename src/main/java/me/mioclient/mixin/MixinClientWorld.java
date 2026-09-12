package me.mioclient.mixin;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.Random;
import me.mioclient.ducks.DuckClientWorldEffects;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.event.events.entity.RemoveEntityEvent;
import me.mioclient.module.render.Ambience;
import me.mioclient.module.render.SkyColor;
import net.minecraft.block.Block;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.BiomeParticleConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({ClientWorld.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinClientWorld.class */
public abstract class MixinClientWorld implements DuckClientWorldEffects {
    private static SkyColor skycolor;

    private static SkyColor getSkycolor() {

        if (skycolor == null && ClientServices.moduleInjector != null) {

            try { skycolor = (SkyColor) ClientServices.moduleInjector.getModule(SkyColor.class); } catch (Exception e) {}

        }

        return skycolor;

    }
    private static Ambience ambience;

    private static Ambience getAmbience() {

        if (ambience == null && ClientServices.moduleInjector != null) {

            try { ambience = (Ambience) ClientServices.moduleInjector.getModule(Ambience.class); } catch (Exception e) {}

        }

        return ambience;

    }

    @Shadow
    @Final
    private DimensionEffects field_24606;

    @Unique
    private final DimensionEffects mio$customEnd = new DimensionEffects.End();

    @Unique
    private final DimensionEffects customSky = new DimensionEffects(Float.NaN, true, DimensionEffects.SkyType.NONE, false, false) { // from class: me.mioclient.mixin.MixinClientWorld.1
        public Vec3d adjustFogColor(Vec3d vec3d, float f) {
        SkyColor skycolor_lcl = getSkycolor();

        if (skycolor_lcl == null) return null;

            return Vec3d.unpackRgb(MixinClientWorld.skycolor.sky.getValue().getRGB());
        }

        public boolean useThickFog(int i, int i2) {
        SkyColor skycolor_lcl = getSkycolor();

        if (skycolor_lcl == null) return false;

            return MixinClientWorld.skycolor.dense.getValue().booleanValue();
        }

        public float[] getFogColorOverride(float f, float f2) {
            return null;
        }
    };

    @Unique
    private final Random random = new Random();

    @Shadow
    public abstract void method_8406(ParticleEffect particleEffect, double d, double d2, double d3, double d4, double d5, double d6);

    @ModifyArg(method = {"method_24462"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    private ParticleEffect randomBlockDisplayTickHook(ParticleEffect particleEffect, @Local(argsOnly = true) BlockPos.Mutable mutable) {
        double squaredDistanceTo = ClientAccessor.minecraftClient.gameRenderer.getCamera().getPos().squaredDistanceTo(mutable.getX(), mutable.getY(), mutable.getZ());
        if (getAmbience().isToggled() && getAmbience().weather.getValue() == Ambience.AmbiencePredicateMode.DUSTY && getAmbience().worldWeather.getValue().booleanValue() && squaredDistanceTo < 25.0d) {
            return new BiomeParticleConfig(this.random.nextBoolean() ? ParticleTypes.WHITE_ASH : ParticleTypes.ASH, 0.1f).getParticle();
        }
        return particleEffect;
    }

    @Inject(method = {"randomBlockDisplayTick"}, at = {@At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V", shift = At.Shift.BEFORE)})
    private void dustParticleTickHook(int i, int i2, int i3, int i4, net.minecraft.util.math.random.Random random, Block block, BlockPos.Mutable mutable, CallbackInfo callbackInfo) {
        Ambience ambience_lcl = getAmbience();

        if (ambience_lcl == null) return;

        if (ambience_lcl.isToggled() && ambience_lcl.weather.getValue() == Ambience.AmbiencePredicateMode.DUSTY && ambience_lcl.worldWeather.getValue().booleanValue()) {
            BiomeParticleConfig biomeParticleConfig = new BiomeParticleConfig(random.nextBoolean() ? ParticleTypes.WHITE_ASH : ParticleTypes.ASH, 0.2f * ambience_lcl.amount.getValue().floatValue());
            if (biomeParticleConfig.shouldAddParticle(random)) {
                method_8406(biomeParticleConfig.getParticle(), mutable.getX() + this.random.nextDouble(), mutable.getY() + this.random.nextDouble(), mutable.getZ() + this.random.nextDouble(), 0.0d, 0.0d, 0.0d);
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

    @Inject(method = {"getSkyColor"}, at = {@At("HEAD")}, cancellable = true)
    private void getSkyColorHook(Vec3d vec3d, float f, CallbackInfoReturnable<Vec3d> callbackInfoReturnable) {
        SkyColor skycolor_lcl = getSkycolor();

        if (skycolor_lcl == null) return;

        if (skycolor_lcl.isToggled() && skycolor_lcl.isDimensionEnabled()) {
            callbackInfoReturnable.setReturnValue(Vec3d.unpackRgb(skycolor_lcl.sky.getValue().getRGB()));
            callbackInfoReturnable.cancel();
        }
    }

    @Inject(method = {"getDimensionEffects"}, at = {@At("HEAD")}, cancellable = true)
    private void onGetSkyProperties(CallbackInfoReturnable<DimensionEffects> callbackInfoReturnable) {
        SkyColor skycolor_lcl = getSkycolor();

        if (skycolor_lcl == null) return;

        if (ClientAccessor.minecraftClient.world == null) {
            return;
        }
        DimensionEffects dimensionEffects = null;
        if (skycolor_lcl.type.getValue() == SkyColor.MixinClientWorldMode.END) {
            dimensionEffects = this.mio$customEnd;
        } else if (!WorldUtils.getDimension().isOverworld() || skycolor_lcl.type.getValue() == SkyColor.MixinClientWorldMode.FLAT) {
            dimensionEffects = this.customSky;
        }
        if (skycolor_lcl.isToggled() && skycolor_lcl.isDimensionEnabled() && dimensionEffects != null) {
            callbackInfoReturnable.setReturnValue(dimensionEffects);
            callbackInfoReturnable.cancel();
        }
    }

    @Override // me.mioclient.ducks.DuckClientWorldEffects
    public DimensionEffects mio$getOriginalEffects() {
        return this.field_24606;
    }
}
